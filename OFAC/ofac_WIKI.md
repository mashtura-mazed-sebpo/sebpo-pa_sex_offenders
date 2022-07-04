OFAC is our most important list, for many reasons. One, the data on the list is very important itself, but two, our clients have many ways to check us. If we are not updating this list promptly and correctly, there could be client complaints. You MUST respond to any failures or Business Team Requests on the same day!

It runs daily, currently at 4pm. While not impossible, there are seldom updates over the weekend. You can sign up on the website to receive a notification of when there are changes.

Note: From the site itself, this data is sometimes refered to as 'sdn'. For example, the file we download an unzip is called 'sdn_advanced.xml'. Therefore, you may see the mixing of OFAC and SDN as names for things.

As of today, the system works like this:

The source key for the list is: OFAC_LIST

The list first calls the scraper: sdn_advanced.groovy

**Which downloads the file: https://www.treasury.gov/ofac/downloads/sanctions/1.0/sdn_advanced.zip

And unzips to reveal: sdn_advanced.xml, and sdn_advanced.xsd.

The xsd was used once, to autogenerate the Java classes. See: http://docs.oracle.com/javase/6/docs/technotes/tools/share/xjc.html

The 'sdn_advanced.xml' file name is put into a listSource object, the ThirdPartySupplier flag is set to true, and the 'OfacSdnFeedDeserializer' is set on the session object as the EntityDeserializer.

The sdn_advanced.groovy script will then run the OfacSdnService java class (which does little except set the location of the temp file), do some cleanup and exit.

Since the ThirdPartySupplier flag was set, the ListScrape class knows to call the deserializer that was set.

Compare the OfacSdnFeedDeserializer to the DowJonesFeedDeserializer. You will see the Ofac is much shorter. To enable the quick correction of any errors, the actual deserialization is done by a groovy script.

All the OfacSdnFeedDeserializer java class does is unmarshall the xml into the classes created with xjc, read the OfacSdnProgramMap from the DB, pull the latest groovy script for 'ofac_advanced_sub_process' by reading from the LI_Scripts table, bind the unmarshalled data and OfacSdnProgramMap to the script, and run with a GroovyScriptEngine. Due to the interface, the deserializer is expected to return an ArrayList (now an ehcache) to the calling code, so the entity cache from the session (which is where the script put its results) is returned.

The MonitoredListOfacSdnProgramMap DB table is a listing of what the program codes we receive in the xml should be mapped to. If a new program is added, the script will fail until the table is updated. It is read fresh every time, so just update and rerun.

insert into MonitoredListOfacSdnProgramMap values ('','') or there is a scraper 'ofac_code_scrapper' which scraps the code from https://home.treasury.gov/policy-issues/financial-sanctions/specially-designated-nationals-list-sdn-list/program-tag-definitions-for-ofac-sanctions-lists and insert in database.

For a normal list, we save the ScrapeEntities to an xml. For most third party data (all_data, dj_soc, OFAC_LIST), we save the source data. This is okay, because you can run XmlDataImporter with the correct flag (-d) and value (thirdparty for all_data, dj_soc for dj_soc, and ofacSdn for OFAC_LIST) and the data can still be loaded. IF you for some reason want the normal xml, you can add the flag -f, with a path and file name, to generate that. But there is really no need.

The source files are saved to: /SG/Internal_Data/List_Importer/data/sdn_advanced/, in dated folders. The xml not in a folder is always the current, and at the moment gets names just '.xml'. Something is chopping off or not setting the rest of the name.

Pulling up a previous file and comparing to current, can help with a lot of data issues. But note, the file is rather large, and is HIGHY NORMALIZED (which means you will have to follow ID numbers all over the place to finally track down all the actual data. You get the hang of it after doing it a few times. Note that when you see something like 'ScriptStatusID="1"' on an element, changing the spacing [add a space before ID] and looking for 'ScriptStatus ID="1"' can be a helpful trick and reveal what it refers to).

This is the list the 'Email Changes' option was recently added for. This is a checkbox in the list admin popup, that if checked, will generate another checkbox on the approval popup. If that is also checked, the APPROVER, will get a nicely formatted email showing all the changes that were just approved. This is then normally mailed out to clients, to show we properly updated. The actual generation of that email is handled by dqa_batch.

## Running OFAC locally

1. IF you do NOT need to test sdn_advanced.groovy, which just pulls and unzips the file from the web, we can use XmlDataImporter to test ofac_advanced_sub_process.groovy:
2. You can download and unzip the file yourself to your local drive.
   - Recall this uses (currently) the data in MonitoredListOfacSdnProgramMap table.
   - Point your local LI to PROD
3 Make sure data in correct in DEV/QA table, and point LI to DEV or QA
4 If you do NOT wish to write data to DB, comment out all code in XmlDataImporter starting at 'ScrapeLoader lScrapeLoader = ' to end of method
5 Run **XmlDataImporter** locally, with program arguments like: OFAC_LIST C:/sdn_advanced.xml -d:ofacSdn -f:c:/ofac_list.xml -r
  - If you choose to write to DB, key name must be correct
  - Update the next arg with the actual path to where you saved the unzipped file from the web
  - the -d option will trigger the OfacSdnFeedDeserializer, which will in turn trigger the ofac_advanced_sub_process.groovy script (where the code you are trying to update probably lives)
This will reach into the DB LI_Scripts table for the version of the ofac_advanced_sub_process.groovy script that goes with key/folderName of 'ofac_advanced_sub_process', so check your environment and versions '''select * from li_scripts ls where scriptname like 'ofac_advanced_sub_process.groovy'''

6. **If you want to run a local copy of ofac_advanced_sub_process.groovy (which is more likely), in the OfacSdnFeedDeserializer class, comment out the code between 'URL tempUrl = null' and 'final URL url = tempUrl', and instead set the tempUrl = "file:///c:/fileLocation"
Put the -r ONLY if you are intending to load to the DB. This way, you can rerun if you need to, and the previous load will be rollback before each new run of XmlDataImporter, without you having to do explicitly in DQA
NEVER CHECK in the code you may have commented out!**

IF you DO want to test sdn_advanced.groovy as well:
Here, you will use ScrapianEngine.

You should be able to copy the List settings from DQA to your local scrapian-config-xx.xml, as for any other list. Make sure of course the com.rdc.importer.me=true flag is set (if not you will, as for all lists, revert to using the settings in the DB). This way, you can point scrapian-config-xx.xml to a local copy of sdn_advanced.groovy.

Again OfacSdnFeedDeserializer will look to the DB to find the right version of ofac_advanced_sub_process for your environment. If you also want to run a local version of that, see the step in the previous section about how to do so.

You may get some errors on the first run or two concerning missing paths. Just create those paths and try again!

Unlike the normal flow of LI, which does not make the standard LI xml for OfacSdn, ScrapianEngine WILL make the xml (if provided the second program argument for that file's location).

While not fully documented here, by judicious changes to ScrapianEngine, you can also use it rather than XmlDataImporter to work on an sdn_advanced.xml file you have retrieved yourself, skipping sdn_advanced.groovy (comment out the actual scrapianEngine.scrape(...), set thirdPartySupplier to true on scrapeSession, make and attach to scrapeSession a ThirdPartyListSource in which you have put the file location [or set String fileName directly], set the OfacSdn deserializer on the scrapeSession, and more ... see, I think easier to do with XmlDataImporter)

================================================================================================= Problem: StackTrace: com.ibatis.common.jdbc.exception.NestedSQLException: --- The error occurred while applying a parameter map. --- Check the deleteTempRecordAndSetRealIDEmend-InlineParameterMap.

Solution: database cleanup "vacuum analyze MonitoredListRecordDataattribute" dqa -> Force Next Run
