package com.rdc.importer;

import net.sf.ehcache.Cache;
//C:\Users\Administrator\Documents\RDC_WEBSCRAPPER-SELENIUM\production\src\main\java\com\rdc\importer\XmlDataImporter.java
import org.springframework.beans.factory.BeanFactory;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.*;
import java.io.*;

import javax.annotation.Resource;

import com.rdc.rdcmodel.dao.MonitoredListDao;
import com.rdc.rdcmodel.model.monitoredlist.MonitoredList;
import com.rdc.rdcmodel.model.monitoredlist.MonitoredListJobStatus;
import com.rdc.scrape.*;
import com.rdc.importer.scrapeloader.*;
import com.rdc.importer.scraper.Deserializer;
import com.rdc.importer.scraper.DowJonesFeedDeserializer;
import com.rdc.importer.scraper.EntityDeserializer;
import com.rdc.importer.scraper.EntityValidationService;
import com.rdc.importer.scraper.OfacSdnFeedDeserializer;
import com.rdc.importer.scraper.ThirdPartyEntityDeserializer;
import com.rdc.importer.scrapian.service.ThirdPartyService;
import com.rdc.importer.scrapian.util.EhCacheUtil;
import com.rdc.jira.service.JiraService;
import com.rdc.core.spring.SpringManager;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

@Component
public class XmlDataImporter {
    private static String DESERIALIZER_THIRD_PARTY = "thirdparty";
    private static String DESERIALIZER_DOW = "dj_soc";
    private static String DESERIALIZER_OFAC_SDN = "ofacSdn";
    private static EntityValidationService entityValidationService;
    private String outputFile;
    private String deserializer;
    private boolean approve;
    private boolean rollback;
    private boolean suppressValidation;
    private boolean xsdSuppressValidation;
    private boolean acceptThirdPartyEvents;
    private static int maxRollBackAttempts = 5;
    private static int currentRollBackAttempt = 0;
    private static Logger logger = LogManager.getLogger(XmlDataImporter.class);
    private String localBaseDir;
    private boolean resolvePriorJira;
    private String encoding;

    @Resource
    public void setConfiguration(Configuration configuration) {
        localBaseDir = configuration.getString("com.rdc.importer.scrapian.localBaseDir");
        resolvePriorJira = configuration.getBoolean("com.rdc.importer.ResolveJiraFromPriorRun", false);
        System.setProperty("ehcache.disk.store.dir", configuration.getString("ehcache.disk.store.dir"));
    }


    public void loadXmlData(String pListName, String pXmlPath) throws Exception {
        String fileName = pXmlPath.replaceAll("\\\\", "/");
        String baseDir = fileName.contains("/") ? fileName.substring(0, fileName.lastIndexOf('/') +1) : (StringUtils.isNotBlank(localBaseDir) ? localBaseDir   : "");
        fileName = fileName.contains("/") ? fileName.substring(fileName.lastIndexOf('/') +1) : fileName;

        String lXmlPath = baseDir + fileName;
        String lListName = pListName;

        logger.info("XMLPATH " + lXmlPath);
        logger.info("LIST NAME " + lListName);
        logger.info("DESERIALIZER " + deserializer);
        logger.info("OUTPUT FILE " + outputFile);

        BeanFactory factory = SpringManager.getInstance().getBeanFactory();
        ScrapeLoaderImpl lScrapeLoaderImpl = (ScrapeLoaderImpl) factory.getBean("scrapeLoaderImpl");
        entityValidationService = (EntityValidationService) factory.getBean("entityValidationService");
        if (approve) {
            logger.info("Approving previous list");
            lScrapeLoaderImpl.approveList(pListName);
            logger.info("Approval complete");
        }
        if (rollback) {
            logger.info("Rolling back previous list");
            currentRollBackAttempt = 0;
            rollback(pListName, lScrapeLoaderImpl);
            logger.info("Roll back complete");
        }

        if (StringUtils.isBlank(lXmlPath) || !(new File(lXmlPath).exists())) {
            throw new Exception("Provide full valid path to your xml file with entities.");
        }

        if (StringUtils.isBlank(lListName)) {
            throw new Exception("Provide name of your script without .groovy extension: dea_fugitives_atlanta");
        }

        ScrapeSession ss = new ScrapeSession(lListName);
        Deserializer lEd = new EntityDeserializer();
        if (StringUtils.isNotBlank(deserializer)) {
            if(encoding == null){
                encoding = "UTF-8";
            }
            if (deserializer.equalsIgnoreCase(DESERIALIZER_THIRD_PARTY)) {
                lEd = new ThirdPartyEntityDeserializer();
                ss.setIsThirdPartySupplier(true);
                if (!xsdSuppressValidation) {
                    ThirdPartyService tps = (ThirdPartyService) factory.getBean("thirdPartyService");
                    tps.getThirdPartyListSource().setSupplier("StaticXsd");
                    tps.validateSchema(new FileInputStream(lXmlPath), lXmlPath);
                }
            } else if (deserializer.equalsIgnoreCase(DESERIALIZER_DOW)) {
                lEd = new DowJonesFeedDeserializer();
                ss.setIsThirdPartySupplier(true);
            } else if (deserializer.equalsIgnoreCase(DESERIALIZER_OFAC_SDN)) {
                lEd = new OfacSdnFeedDeserializer();
                ss.setIsThirdPartySupplier(true);
            } else {
                throw new Exception("Unknown deserializer " + deserializer + ".");
            }
        }
        if (lEd instanceof ThirdPartyEntityDeserializer && acceptThirdPartyEvents) {
            ((ThirdPartyEntityDeserializer) lEd).setLoadEventCodes(true);
        }
        if (encoding == null) {
            BufferedReader br = new BufferedReader(new FileReader(lXmlPath));
            String line;
            if ((line = br.readLine()) != null && line.contains("encoding=")) {
                line = line.replaceAll(".*encoding=\"(.*?)\".*", "$1");
                if (StringUtils.isNotBlank(line)) {
                    encoding = line;
                }
            }
            br.close();
        }
        if(encoding == null){
            encoding = System.getProperty("file.encoding", "windows-1252");
        }
        ss.setEncoding(encoding);
        Cache lEntitiesCache = lEd.deserializeToCache(new FileInputStream(lXmlPath), encoding);

        lEd = null;

        if (StringUtils.isNotBlank(deserializer) && (deserializer.equalsIgnoreCase(DESERIALIZER_DOW) || deserializer.equalsIgnoreCase(DESERIALIZER_OFAC_SDN))) {
            HashSet<String> IDS = new HashSet<>();



            for (Object key : lEntitiesCache.getKeys()) {
                ScrapeEntity se = (ScrapeEntity) (lEntitiesCache.get(key).getValue());
                if (StringUtils.isBlank(se.getId())) {
                    throw new Exception("Id must not be blank for DOW: " + se.getName());
                } else {
                    IDS.add(se.getId());
                }
            }
            for (Object key : lEntitiesCache.getKeys()) {
                ScrapeEntity se = (ScrapeEntity) (lEntitiesCache.get(key).getValue());
                if (se.getScrapeEntityAssociations() != null && se.getScrapeEntityAssociations().isEmpty()) {
                    for (ScrapeEntityAssociation sea : se.getScrapeEntityAssociations()) {
                        if (!IDS.contains(sea.getId())) {
                            throw new Exception("Entity given as association does not have ID existing in pool: " + se.getName() + " contains an association, but association ID not found.");
                        }
                    }
                }
            }
        }

        FileOutputStream outputStream = null;
        Exception ex = null;
        try {
            ss.setEntities(lEntitiesCache);
            if (StringUtils.isNotBlank(outputFile)) {
                outputStream = new FileOutputStream(outputFile);
                ss.setEscape(false);
                ss.setEscapeSpecial(true);
                ss.dump(outputStream);
                //validation below will catch character issues.  No reason to do here.
            }
            validateEntities(ss);
        } catch (Exception e) {
            logger.info(e.getMessage());
            ex = e;
        } finally {
            if (outputStream != null) {
                IOUtils.closeQuietly(outputStream);
            }
        }
        ss = null;
/*        ScrapeLoader lScrapeLoader = (ScrapeLoader) factory.getBean("scrapeLoaderImpl");

        ChangeSummary lCs = null;

        lCs = lScrapeLoader.loadList(lListName, lEntitiesCache, true);

        logger.info("\n\nRECORDS\n\tDELETED " + lCs.getRecordsDeleted() + "\n\tADDED " + lCs.getRecordsAdded() + "\n\tCHANGED " + lCs.getRecordsChanged() + "\n\n");

        EhCacheUtil.getCacheManager().removeAllCaches();
        try {
            MonitoredListDao monitoredListDao = (MonitoredListDao) SpringManager.getInstance().getBeanFactory().getBean("monitoredListDaoImpl");
            writeMonitoredListJobStatus(lListName, lCs, monitoredListDao);
            if (resolvePriorJira) {
                JiraService jiraService = (JiraService) SpringManager.getInstance().getBeanFactory().getBean("jiraService");
                MonitoredList monitoredList = monitoredListDao.getMonitoredListByFolderName(lListName).getMonitoredList();
                String summary = pListName + (monitoredList != null && monitoredList.getPriorityId() != null ? " P:" + monitoredList.getPriorityId() : "") + " - " + ListLoadResult.WebListStatus.IMPORT_FAILURE;
                ArrayList<String> lClosedKeys = jiraService.resolveAutoCreatedIssues(summary, "LI");

                logger.info("TICKETS CLOSED: " + lClosedKeys.toString());
            }
        } catch (Exception e) {
            logger.info(e);
            e.printStackTrace();
        }
        if(ex != null){
            throw ex;
        }*/
    }

    private static void writeMonitoredListJobStatus(String monitoredListFolderName, ChangeSummary lCs, MonitoredListDao monitoredListDao) throws SQLException {
        MonitoredListJobStatus jobStatus = new MonitoredListJobStatus();
        jobStatus.setFolderName(monitoredListFolderName);

        jobStatus.setRunTime(-1L);
        jobStatus.setErrors(false);

        if (lCs.getRecordsAdded() > 0 || lCs.getRecordsChanged() > 0 || lCs.getRecordsDeleted() > 0) {
            jobStatus.setChanges(true);
            jobStatus.setRecordsAdded(lCs.getRecordsAdded());
            jobStatus.setRecordsDeleted(lCs.getRecordsDeleted());
            jobStatus.setRecordsChanged(lCs.getRecordsChanged());
        }
        monitoredListDao.addNewMonitoredListJobStatus(jobStatus);
    }

    private void validateEntities(ScrapeSession scrapeSession) throws Exception {
        List<String> errors = entityValidationService.validate(scrapeSession.getEntities());
        if (!errors.isEmpty()) {
            StringBuffer buffer = new StringBuffer();
            for (String error : errors) {
                buffer.append(error).append("\n");
            }
            String valError = "Validation Errors Occurred:\n";
            if (!scrapeSession.isThirdPartySupplier()) {
                if (!suppressValidation) {
                    throw new Exception( valError + buffer.toString());
                } else {
                    logger.info(valError + errors);
                }
            } else {
                logger.info(valError + errors);
            }
        }
    }

    public void handleArg(String arg) {
        if (arg == null) {
            return;
        }
        if (arg.startsWith("-d:")) {
            deserializer = arg.substring(3);
        } else if (arg.startsWith("-f:")) {
            outputFile = arg.substring(3);
        }else if (arg.startsWith("-n:")) {
            encoding = arg.substring(3);
        } else if (arg.startsWith("-a")) {
            approve = true;
        } else if (arg.startsWith("-r")) {
            rollback = true;
        } else if (arg.startsWith("-s")) {
            suppressValidation = true;
        } else if (arg.startsWith("-x")) {
            xsdSuppressValidation = true;
        } else if (arg.startsWith("-e")) {
            acceptThirdPartyEvents = true;
        }
    }

    public static void rollback(String pListName, ScrapeLoaderImpl lScrapeLoaderImpl) throws SQLException {
        currentRollBackAttempt++;
        try {
            lScrapeLoaderImpl.rollBackList(pListName);
        } catch (SQLException e) {
            if (currentRollBackAttempt < maxRollBackAttempts) {
                logger.info("Roll back attempt " + (currentRollBackAttempt + 1));
                rollback(pListName, lScrapeLoaderImpl);
            } else
                throw e;
        }
    }


    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Usage: java com.rdc.importer.XmlDataImporter list_name path_to_entities_xml_file -d:[deserializer] -f:[outputfile] -n:[charEncoding]");
            System.out.println("-a (approve) -r (rollback) -s (suppress Entity Validation) -x (suppress xsd validation) -e (accept thirdParty events)");
            System.out.println("Example: java com.rdc.importer.XmlDataImporter dea_fugitives data_dump.xml -d:thirdparty");

            logger.info("Usage: java com.rdc.importer.XmlDataImporter list_name path_to_entities_xml_file -d:[deserializer] -f:[outputfile] -n:[charEncoding]");
            logger.info("-a (approve) -r (rollback) -s (suppress Entity Validation) -x (suppress xsd validation) -e (accept thirdParty events)");
            logger.info("Example: java com.rdc.importer.XmlDataImporter dea_fugitives data_dump.xml -d:thirdparty");
            System.exit(1);
        }
        XmlDataImporter xmlDataImporter = (XmlDataImporter) SpringManager.getInstance().getBeanFactory().getBean("xmlDataImporter");
        for (int i = 2; i < args.length; i++) {
            xmlDataImporter.handleArg(args[i]);
        }

        xmlDataImporter.loadXmlData(args[0], args[1]);
        System.exit(0);
    }

}