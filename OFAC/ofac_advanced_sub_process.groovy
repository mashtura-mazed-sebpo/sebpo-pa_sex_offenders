import com.rdc.core.nameparser.PersonName
import com.rdc.core.nameparser.PersonNameParserImpl
import com.rdc.importer.thirdparty.sdn.*
import com.rdc.importer.thirdparty.sdn.DistinctPartySchemaType.Profile
import com.rdc.importer.thirdparty.sdn.DocumentedNameSchemaType.DocumentedNamePart.NamePartValue
import com.rdc.importer.thirdparty.sdn.FeatureSchemaType.FeatureVersion.VersionDetail
import com.rdc.importer.thirdparty.sdn.IdentitySchemaType.Alias
import com.rdc.importer.thirdparty.sdn.IdentitySchemaType.NamePartGroups.MasterNamePartGroup
import com.rdc.importer.thirdparty.sdn.LocationSchemaType.LocationPart
import com.rdc.importer.thirdparty.sdn.LocationSchemaType.LocationPart.LocationPartValue
import com.rdc.importer.thirdparty.sdn.ReferenceValueSetsSchemaType.AliasTypeValues.AliasType
import com.rdc.importer.thirdparty.sdn.ReferenceValueSetsSchemaType.AreaCodeTypeValues.AreaCodeType
import com.rdc.importer.thirdparty.sdn.ReferenceValueSetsSchemaType.AreaCodeValues.AreaCode
import com.rdc.importer.thirdparty.sdn.ReferenceValueSetsSchemaType.CalendarTypeValues.CalendarType
import com.rdc.importer.thirdparty.sdn.ReferenceValueSetsSchemaType.CountryRelevanceValues.CountryRelevance
import com.rdc.importer.thirdparty.sdn.ReferenceValueSetsSchemaType.CountryValues.Country
import com.rdc.importer.thirdparty.sdn.ReferenceValueSetsSchemaType.DecisionMakingBodyValues.DecisionMakingBody
import com.rdc.importer.thirdparty.sdn.ReferenceValueSetsSchemaType.DetailReferenceValues.DetailReference
import com.rdc.importer.thirdparty.sdn.ReferenceValueSetsSchemaType.DetailTypeValues.DetailType
import com.rdc.importer.thirdparty.sdn.ReferenceValueSetsSchemaType.DocNameStatusValues.DocNameStatus
import com.rdc.importer.thirdparty.sdn.ReferenceValueSetsSchemaType.EntryEventTypeValues.EntryEventType
import com.rdc.importer.thirdparty.sdn.ReferenceValueSetsSchemaType.EntryLinkTypeValues.EntryLinkType
import com.rdc.importer.thirdparty.sdn.ReferenceValueSetsSchemaType.ExRefTypeValues.ExRefType
import com.rdc.importer.thirdparty.sdn.ReferenceValueSetsSchemaType.FeatureTypeGroupValues.FeatureTypeGroup
import com.rdc.importer.thirdparty.sdn.ReferenceValueSetsSchemaType.FeatureTypeValues.FeatureType
import com.rdc.importer.thirdparty.sdn.ReferenceValueSetsSchemaType.IDRegDocDateTypeValues.IDRegDocDateType
import com.rdc.importer.thirdparty.sdn.ReferenceValueSetsSchemaType.IDRegDocTypeValues.IDRegDocType
import com.rdc.importer.thirdparty.sdn.ReferenceValueSetsSchemaType.IdentityFeatureLinkTypeValues.IdentityFeatureLinkType
import com.rdc.importer.thirdparty.sdn.ReferenceValueSetsSchemaType.LegalBasisTypeValues.LegalBasisType
import com.rdc.importer.thirdparty.sdn.ReferenceValueSetsSchemaType.LegalBasisValues.LegalBasis
import com.rdc.importer.thirdparty.sdn.ReferenceValueSetsSchemaType.LocPartTypeValues
import com.rdc.importer.thirdparty.sdn.ReferenceValueSetsSchemaType.LocPartTypeValues.LocPartType
import com.rdc.importer.thirdparty.sdn.ReferenceValueSetsSchemaType.LocPartValueStatusValues.LocPartValueStatus
import com.rdc.importer.thirdparty.sdn.ReferenceValueSetsSchemaType.LocPartValueTypeValues.LocPartValueType
import com.rdc.importer.thirdparty.sdn.ReferenceValueSetsSchemaType.NamePartTypeValues.NamePartType
import com.rdc.importer.thirdparty.sdn.ReferenceValueSetsSchemaType.OrganisationValues.Organisation
import com.rdc.importer.thirdparty.sdn.ReferenceValueSetsSchemaType.PartySubTypeValues.PartySubType
import com.rdc.importer.thirdparty.sdn.ReferenceValueSetsSchemaType.PartyTypeValues.PartyType
import com.rdc.importer.thirdparty.sdn.ReferenceValueSetsSchemaType.RelationQualityValues.RelationQuality
import com.rdc.importer.thirdparty.sdn.ReferenceValueSetsSchemaType.RelationTypeValues.RelationType
import com.rdc.importer.thirdparty.sdn.ReferenceValueSetsSchemaType.ReliabilityValues.Reliability
import com.rdc.importer.thirdparty.sdn.ReferenceValueSetsSchemaType.SanctionsProgramValues.SanctionsProgram
import com.rdc.importer.thirdparty.sdn.ReferenceValueSetsSchemaType.SanctionsTypeValues.SanctionsType
import com.rdc.importer.thirdparty.sdn.ReferenceValueSetsSchemaType.ScriptStatusValues.ScriptStatus
import com.rdc.importer.thirdparty.sdn.ReferenceValueSetsSchemaType.SubsidiaryBodyValues.SubsidiaryBody
import com.rdc.importer.thirdparty.sdn.ReferenceValueSetsSchemaType.SupInfoTypeValues.SupInfoType
import com.rdc.importer.thirdparty.sdn.ReferenceValueSetsSchemaType.TargetTypeValues.TargetType
import com.rdc.importer.thirdparty.sdn.ReferenceValueSetsSchemaType.ValidityValues.Validity
import com.rdc.importer.thirdparty.sdn.SanctionsEntrySchemaType.EntryEvent
import com.rdc.importer.thirdparty.sdn.SanctionsEntrySchemaType.SanctionsMeasure
import com.rdc.rdcmodel.model.RelationshipType
import com.rdc.scrape.*
import org.apache.commons.lang.StringUtils

context.session.encoding = 'UTF-8'

_1 = new BigInteger("1");
_12 = new BigInteger("12");
_31 = new BigInteger("31");
counter = 1;

aliasTypeMap = new HashMap<BigInteger, String>();
aliasMap = new HashMap<BigInteger, ArrayList<String>>();
areaCodeMap = new HashMap<BigInteger, String>();
areaCodeTypeMap = new HashMap<BigInteger, String>();
calendarTypeMap = new HashMap<BigInteger, String>();
countryMap = new HashMap<BigInteger, String>();
countryRelevanceMap = new HashMap<BigInteger, String>();
decisionMakingBodyMap = new HashMap<BigInteger, DecisionMakingBody>();
detailReferenceMap = new HashMap<BigInteger, String>();
detailTypeMap = new HashMap<BigInteger, String>();
docNameStatusMap = new HashMap<BigInteger, String>();
entryEventTypeMap = new HashMap<BigInteger, String>();
entryLinkTypeMap = new HashMap<BigInteger, String>();
exRefTypeMap = new HashMap<BigInteger, String>();
featureTypeMap = new HashMap<BigInteger, String>();
featureTypeGroupMap = new HashMap<BigInteger, String>();
iDRegDocDateTypeMap = new HashMap<BigInteger, String>();
iDRegDocTypeMap = new HashMap<BigInteger, String>();
identityFeatureLinkTypeMap = new HashMap<BigInteger, String>();
legalBasisMap = new HashMap<BigInteger, String>();
legalBasisTypeMap = new HashMap<BigInteger, String>();
listMap = new HashMap<BigInteger, String>();
locPartTypeMap = new HashMap<BigInteger, String>();
locPartValueStatusMap = new HashMap<BigInteger, String>();
locPartValueTypeMap = new HashMap<BigInteger, String>();
namePartTypeMap = new HashMap<BigInteger, String>();
organisationMap = new HashMap<BigInteger, String>();
partySubTypeMap = new HashMap<BigInteger, PartySubType>();
partyTypeMap = new HashMap<BigInteger, String>();
relationQualityMap = new HashMap<BigInteger, String>();
relationTypeMap = new HashMap<BigInteger, String>();
reliabilityMap = new HashMap<BigInteger, String>();
//private HashMap<BigInteger, String> sanctionsProgramMap = new HashMap<BigInteger, String>();
sanctionsTypeMap = new HashMap<BigInteger, String>();
//scriptMap = new HashMap<BigInteger, String>();
scriptStatusMap = new HashMap<BigInteger, String>();
subsidiaryBodyMap = new HashMap<BigInteger, SubsidiaryBody>();
supInfoTypeMap = new HashMap<BigInteger, String>();
targetTypeMap = new HashMap<BigInteger, String>();
validityMap = new HashMap<BigInteger, String>();

featureList = new HashSet<String>();
personNameParser = new PersonNameParserImpl();
String addressRemark = "";

List<FeatureType> ft = root.getReferenceValueSets().getFeatureTypeValues().getFeatureType();
featureTypeHash = new HashMap<BigInteger, String>();
for (FeatureType f : ft) {
    featureTypeHash.put(f.getID(), f.getValue());
}

List<AliasType> aliasTypeList = root.getReferenceValueSets().getAliasTypeValues().getAliasType();
List<AreaCode> areaCodeList = root.getReferenceValueSets().getAreaCodeValues().getAreaCode();
List<AreaCodeType> areaCodeTypeList = root.getReferenceValueSets().getAreaCodeTypeValues().getAreaCodeType();
List<CalendarType> calendarTypeList = root.getReferenceValueSets().getCalendarTypeValues().getCalendarType();
List<Country> countryList = root.getReferenceValueSets().getCountryValues().getCountry();
List<CountryRelevance> countryRelevanceList = root.getReferenceValueSets().getCountryRelevanceValues().getCountryRelevance();
List<DecisionMakingBody> decisionMakingBodyList = root.getReferenceValueSets().getDecisionMakingBodyValues().getDecisionMakingBody();
List<DetailReference> detailReferenceList = root.getReferenceValueSets().getDetailReferenceValues().getDetailReference();
List<DetailType> detailTypeList = root.getReferenceValueSets().getDetailTypeValues().getDetailType();
List<DocNameStatus> docNameStatusList = root.getReferenceValueSets().getDocNameStatusValues().getDocNameStatus();
List<EntryEventType> entryEventTypeList = root.getReferenceValueSets().getEntryEventTypeValues().getEntryEventType();
List<EntryLinkType> entryLinkTypeList = root.getReferenceValueSets().getEntryLinkTypeValues().getEntryLinkType();
List<ExRefType> exRefTypeList = root.getReferenceValueSets().getExRefTypeValues().getExRefType();
List<FeatureType> featureTypeList = root.getReferenceValueSets().getFeatureTypeValues().getFeatureType();
List<FeatureTypeGroup> featureTypeGroupList = root.getReferenceValueSets().getFeatureTypeGroupValues().getFeatureTypeGroup();
List<IDRegDocDateType> iDRegDocDateTypeList = root.getReferenceValueSets().getIDRegDocDateTypeValues().getIDRegDocDateType();
List<IDRegDocType> iDRegDocTypeList = root.getReferenceValueSets().getIDRegDocTypeValues().getIDRegDocType();
List<IdentityFeatureLinkType> identityFeatureLinkTypeList = root.getReferenceValueSets().getIdentityFeatureLinkTypeValues().getIdentityFeatureLinkType();
List<LegalBasis> legalBasisList = root.getReferenceValueSets().getLegalBasisValues().getLegalBasis();
List<LegalBasisType> legalBasisTypeList = root.getReferenceValueSets().getLegalBasisTypeValues().getLegalBasisType();
List<ReferenceValueSetsSchemaType.ListValues.List> listList = root.getReferenceValueSets().getListValues().getList();
List<LocPartType> locPartTypeList = root.getReferenceValueSets().getLocPartTypeValues().getLocPartType();
List<LocPartValueStatus> locPartValueStatusList = root.getReferenceValueSets().getLocPartValueStatusValues().getLocPartValueStatus();
List<LocPartValueType> locPartValueTypeList = root.getReferenceValueSets().getLocPartValueTypeValues().getLocPartValueType();
List<NamePartType> namePartTypeList = root.getReferenceValueSets().getNamePartTypeValues().getNamePartType();
List<Organisation> organisationList = root.getReferenceValueSets().getOrganisationValues().getOrganisation();
List<PartySubType> partySubTypeList = root.getReferenceValueSets().getPartySubTypeValues().getPartySubType();
List<PartyType> partyTypeList = root.getReferenceValueSets().getPartyTypeValues().getPartyType();
List<RelationQuality> relationQualityList = root.getReferenceValueSets().getRelationQualityValues().getRelationQuality();
List<RelationType> relationTypeList = root.getReferenceValueSets().getRelationTypeValues().getRelationType();
List<Reliability> reliabilityList = root.getReferenceValueSets().getReliabilityValues().getReliability();
List<SanctionsProgram> sanctionsProgramList = root.getReferenceValueSets().getSanctionsProgramValues().getSanctionsProgram();
List<SanctionsType> sanctionsTypeList = root.getReferenceValueSets().getSanctionsTypeValues().getSanctionsType();
List<ReferenceValueSetsSchemaType.ScriptValues.Script> scriptList = root.getReferenceValueSets().getScriptValues().getScript();
List<ScriptStatus> scriptStatusList = root.getReferenceValueSets().getScriptStatusValues().getScriptStatus();
List<SubsidiaryBody> subsidiaryBodyList = root.getReferenceValueSets().getSubsidiaryBodyValues().getSubsidiaryBody();
List<SupInfoType> supInfoTypeList = root.getReferenceValueSets().getSupInfoTypeValues().getSupInfoType();
List<TargetType> targetTypeList = root.getReferenceValueSets().getTargetTypeValues().getTargetType();
List<Validity> validityList = root.getReferenceValueSets().getValidityValues().getValidity();

for (AliasType temp : aliasTypeList) {
    aliasTypeMap.put(temp.getID(), temp.getValue());
}
for (AreaCode temp : areaCodeList) {
    areaCodeMap.put(temp.getID(), temp.getValue());
}
for (AreaCodeType temp : areaCodeTypeList) {
    areaCodeTypeMap.put(temp.getID(), temp.getValue());
}
for (CalendarType temp : calendarTypeList) {
    calendarTypeMap.put(temp.getID(), temp.getValue());
}
for (Country temp : countryList) {
    countryMap.put(temp.getID(), temp.getValue());
}
for (CountryRelevance temp : countryRelevanceList) {
    countryRelevanceMap.put(temp.getID(), temp.getValue());
}
for (DecisionMakingBody temp : decisionMakingBodyList) {
    decisionMakingBodyMap.put(temp.getID(), temp);
}
for (DetailReference temp : detailReferenceList) {
    detailReferenceMap.put(temp.getID(), temp.getValue());
}
for (DetailType temp : detailTypeList) {
    detailTypeMap.put(temp.getID(), temp.getValue());
}
for (DocNameStatus temp : docNameStatusList) {
    docNameStatusMap.put(temp.getID(), temp.getValue());
}
for (EntryEventType temp : entryEventTypeList) {
    entryEventTypeMap.put(temp.getID(), temp.getValue());
}
for (EntryLinkType temp : entryLinkTypeList) {
    entryLinkTypeMap.put(temp.getID(), temp.getValue());
}
for (ExRefType temp : exRefTypeList) {
    exRefTypeMap.put(temp.getID(), temp.getValue());
}
for (FeatureType temp : featureTypeList) {
    featureTypeMap.put(temp.getID(), temp.getValue());
}
for (FeatureTypeGroup temp : featureTypeGroupList) {
    featureTypeGroupMap.put(temp.getID(), temp.getValue());
}
for (IDRegDocDateType temp : iDRegDocDateTypeList) {
    iDRegDocDateTypeMap.put(temp.getID(), temp.getValue());
}
for (IDRegDocType temp : iDRegDocTypeList) {
    iDRegDocTypeMap.put(temp.getID(), temp.getValue());
}
for (IdentityFeatureLinkType temp : identityFeatureLinkTypeList) {
    identityFeatureLinkTypeMap.put(temp.getID(), temp.getValue());
}
for (LegalBasis temp : legalBasisList) {
    legalBasisMap.put(temp.getID(), temp.getValue());
}
for (LegalBasisType temp : legalBasisTypeList) {
    legalBasisTypeMap.put(temp.getID(), temp.getValue());
}
for (ReferenceValueSetsSchemaType.ListValues.List temp : listList) {
    listMap.put(temp.getID(), temp.getValue());
}
for (LocPartType temp : locPartTypeList) {
    locPartTypeMap.put(temp.getID(), temp.getValue());
}
for (LocPartValueStatus temp : locPartValueStatusList) {
    locPartValueStatusMap.put(temp.getID(), temp.getValue());
}
for (LocPartValueType temp : locPartValueTypeList) {
    locPartValueTypeMap.put(temp.getID(), temp.getValue());
}
for (NamePartType temp : namePartTypeList) {
    namePartTypeMap.put(temp.getID(), temp.getValue());
}
if (namePartTypeMap.size() == 0) {
    throw new Exception("OfacSdn");
}
for (Organisation temp : organisationList) {
    organisationMap.put(temp.getID(), temp.getValue());
}
for (PartySubType temp : partySubTypeList) {
    partySubTypeMap.put(temp.getID(), temp);
}
for (PartyType temp : partyTypeList) {
    partyTypeMap.put(temp.getID(), temp.getValue());
}
for (RelationQuality temp : relationQualityList) {
    relationQualityMap.put(temp.getID(), temp.getValue());
}
for (RelationType temp : relationTypeList) {
    relationTypeMap.put(temp.getID(), temp.getValue());
}
for (Reliability temp : reliabilityList) {
    reliabilityMap.put(temp.getID(), temp.getValue());
}
//		for (SanctionsProgram temp : sanctionsProgramList) {
//			sanctionsProgramMap.put(temp.getID(), temp.getValue());
//		}
for (SanctionsType temp : sanctionsTypeList) {
    sanctionsTypeMap.put(temp.getID(), temp.getValue());
}
//for (ReferenceValueSetsSchemaType.ScriptValues.Script temp : scriptList) {
//	scriptMap.put(temp.getID(), temp.getValue());
//}
for (ScriptStatus temp : scriptStatusList) {
    scriptStatusMap.put(temp.getID(), temp.getValue());
}
for (SubsidiaryBody temp : subsidiaryBodyList) {
    subsidiaryBodyMap.put(temp.getID(), temp);
}
for (SupInfoType temp : supInfoTypeList) {
    supInfoTypeMap.put(temp.getID(), temp.getValue());
}
for (TargetType temp : targetTypeList) {
    targetTypeMap.put(temp.getID(), temp.getValue());
}
for (Validity temp : validityList) {
    validityMap.put(temp.getID(), temp.getValue());
}


//Names
//		AliasTypeValues aliasList = root.getReferenceValueSets().getAliasTypeValues();
//		PartySubTypeValues partySubTypeList = root.getReferenceValueSets().getPartySubTypeValues();
//		PartyTypeValues partyTypeList = root.getReferenceValueSets().getPartyTypeValues();
//		NamePartTypeValues namePartTypeList = root.getReferenceValueSets().getNamePartTypeValues();

//there are other alias types, but as of yet not info we want/can capture
aliasNameTypeId = new BigInteger("-1");
for (AliasType at : aliasTypeList) {
    if (at.getValue().equals("Name")) {
        aliasNameTypeId = at.getID();
    }
}
if (aliasNameTypeId.intValue() == -1) {
    throw new Exception("Could not parse OfacSdn!");
}

docNameStatusID = new BigInteger("-1");
for (DocNameStatus dns : docNameStatusList) {
    if (dns.getValue().equals("Primary Latin")) {
        docNameStatusID = dns.getID();
    }
}
if (docNameStatusID.intValue() == -1) {
    throw new Exception("Could not parse OfacSdn!");
}

//Entity Type
BigInteger individualTypeId = new BigInteger("-1");
for (PartyType pt : partyTypeList) {
    if (pt.getValue().equals("Individual")) {
        individualTypeId = pt.getID();
    }
}

List<BigInteger> individualSubTypeIds = new ArrayList<BigInteger>();
for (PartySubType pt : partySubTypeList) {
    if (pt.getPartyTypeID().equals(individualTypeId)) {
        individualSubTypeIds.add(pt.getID());
    }
}

//details
//		detailMap = new HashMap<BigInteger, String>();
//		for(DetailReference dr : root.getReferenceValueSets().getDetailReferenceValues().getDetailReference()){
//			detailMap.put(dr.getID(), dr.getValue());
//		}

//country
List<Country> countryCodes = root.getReferenceValueSets().getCountryValues().getCountry();
countryMap = new HashMap<BigInteger, String>();
for (Country country : countryCodes) {
    countryMap.put(country.getID(), country.getValue());
}

//sanctions
//		sanctionsProgramMap = new HashMap<BigInteger, String>();
//		for(SanctionsProgram sp : root.getReferenceValueSets().getSanctionsProgramValues().getSanctionsProgram()){
//			SubsidiaryBody subBod = subsidiaryBodyMap.get(sp.getSubsidiaryBodyID());
//			DecisionMakingBody decBod = decisionMakingBodyMap.get(subBod.getDecisionMakingBodyID());
//			//String org = organisationMap.get(decBod.getOrganisationID());
//			if(!decBod.getValue().equals("Unknown")){
//				sanctionsProgramMap.put(sp.getID(), decBod.getValue());
//			}
//		}
sanctionsTypeMap = new HashMap<BigInteger, String>();
for (SanctionsType st : root.getReferenceValueSets().getSanctionsTypeValues().getSanctionsType()) {
    sanctionsTypeMap.put(st.getID(), st.getValue());
}
legalBasisMap = new HashMap<BigInteger, String>();
for (LegalBasis lb : root.getReferenceValueSets().getLegalBasisValues().getLegalBasis()) {
    //String sancProg = sanctionsProgramMap.get(lb.getSanctionsProgramID());
    String legalBasis = lb.getValue();
    if (legalBasis != null && !legalBasis.toUpperCase().equals("UNKNOWN")) {
        legalBasisMap.put(lb.getID(), legalBasis);
    }
}

sanctionsMap = new HashMap<BigInteger, ArrayList<ScrapeEvent>>();
for (SanctionsEntrySchemaType sest : root.getSanctionsEntries().getSanctionsEntry()) {
    StringBuilder sb = new StringBuilder();
    sb.append("This entity appears on the United States Treasury Department\u2019s Office of Foreign Assets Control (OFAC) SDN List");
    ScrapeEvent event = new ScrapeEvent();
    //				String theList = listMap.get(sest.getListID());
    //				if(StringUtils.isNotBlank(theList)){
    //					sb.append("List: " + theList);
    //				}
    boolean legalSet = false;
    boolean multiplePrograms = false;
    for (EntryEvent ee : sest.getEntryEvent()) {
        String eventType = entryEventTypeMap.get(ee.getEntryEventTypeID());
        if ("Created".equals(eventType)) {

            DateSchemaType dpst = ee.getDate();
            Integer year = -1, day = -1, month = -1;
            BigInteger temp = dpst.getDay().getValue();
            if (temp != null) {
                day = temp.intValue();
            }
            temp = dpst.getMonth().getValue();
            if (temp != null) {
                month = temp.intValue();
            }
            temp = dpst.getYear().getValue();
            if (temp != null) {
                year = temp.intValue();
            }
            if (year == -1 || month == -1 || day == -1) {
                throw new Exception("Bad dates for entry event " + ee.getID());
            }
            event.setDate((month < 10 ? "0" : "") + month + "/" + (day < 10 ? "0" : "") + day + "/" + year);

            String legalBasis = legalBasisMap.get(ee.getLegalBasisID());
            if (StringUtils.isNotBlank(legalBasis)) {
                sb.append(", on the legal basis of " + legalBasis);
                legalSet = true;
            }
            String comment = ee.getComment().getValue();
            if (StringUtils.isNotBlank(comment)) {
                String mapped = monitoredListOfacSdnProgramMap.get(comment.toUpperCase());
                if (mapped == null) {
                    throw new Exception("Did not have mapping for " + comment);
                }
                if (sb.toString().contains(" under program ")) {
                    multiplePrograms = true;
                }
                sb.append((sb.toString().contains(" under program ") ?
                    " and " + comment + " (" + mapped + ")"
                    :
                    (StringUtils.isNotBlank(legalBasis) ? " and " : ", ") + "under program " + comment + " (" + mapped + ")"
                ));
            }
        }
    }


    for (SanctionsMeasure sm : sest.getSanctionsMeasure()) {
        Comment comment = sm.getComment();
        String sancType = sanctionsTypeMap.get(sm.getSanctionsTypeID());
        if (StringUtils.isNotBlank(sancType)) {
            if (comment != null && StringUtils.isNotBlank(comment.getValue())) {
                if (sancType.equals("Program")) {
                    String mapped = monitoredListOfacSdnProgramMap.get(comment.getValue().toUpperCase());
                    if (mapped == null) {
                        throw new Exception("Did not have mapping for " + comment.getValue());
                    }
                    if (sb.toString().contains(" under program ")) {
                        multiplePrograms = true;
                    }
                    sb.append((sb.toString().contains(" under program ") ?
                        " and " + comment.getValue() + " (" + mapped + ")"
                        :
                        (legalSet ? " and " : ", ") + "under program " + comment.getValue() + " (" + mapped + ")"
                    ));
                }
            }
        } else {
            throw new Exception("Blank santionType for " + sm.getSanctionsTypeID());
        }
    }
    String eventDesc = sb.toString();
    if (multiplePrograms) {
        eventDesc = eventDesc.replace(" under program ", " under programs ");
    }

    ArrayList<ScrapeEvent> sancs = sanctionsMap.get(sest.getProfileID());
    if (sancs == null) {
        sancs = new ArrayList<ScrapeEvent>();
        sanctionsMap.put(sest.getProfileID(), sancs);
    }
    event.setDescription(eventDesc.length() > 750 ? eventDesc.substring(0, 747) + "..." : eventDesc);
    sancs.add(event);
}

//docs
docTypeMap = new HashMap<BigInteger, String>();
for (IDRegDocType dt : root.getReferenceValueSets().getIDRegDocTypeValues().getIDRegDocType()) {
    docTypeMap.put(dt.getID(), dt.getValue());
}

docMap = new HashMap<BigInteger, ArrayList<ScrapeIdentification>>();
for (IDRegDocumentSchemaType doc : root.getIDRegDocuments().getIDRegDocument()) {
    ScrapeIdentification si = new ScrapeIdentification();
    StringBuilder tempSb = new StringBuilder();

    BigInteger typeID = doc.getIDRegDocTypeID();
    if (typeID != null) {
        String type = docTypeMap.get(typeID);
        if (StringUtils.isBlank(type)) {
            throw new Exception("Blank doc type for " + doc.getID());
        }
        tempSb.append(type);
    }
    String validity = validityMap.get(doc.getValidityID());
    if (StringUtils.isNotBlank(validity)) {
        tempSb.append(" (" + validity + ")");
    }
    String issuer = doc.getIssuingAuthority().getValue();
    if (StringUtils.isNotBlank(issuer)) {
        tempSb.append(" (" + issuer + ")");
    }
    //			Comment comment = doc.getComment();
    //			if(comment != null && StringUtils.isNotBlank(comment.getValue())){
    //				tempSb.append(" (" + comment.getValue() + ")");
    //			}
    si.setType(tempSb.length() > 100 ? tempSb.toString().substring(0, 97) + "..." : tempSb.toString());
    String regNo = doc.getIDRegistrationNo().getValue();
    if (StringUtils.isNotBlank(regNo)) {
        si.setValue(regNo);
    }
    BigInteger countryID = doc.getIssuedByCountryID();
    if (countryID != null) {
        String country = countryMap.get(countryID);
        if (StringUtils.isBlank(country)) {
            throw new Exception("Blank country for " + countryID);
        }
        si.setCountry(country);
    }
    //			List<DocumentDate> dates = doc.getDocumentDate();
    //			if(dates.size() > 0){
    //				if(dates.size() > 2){
    //					throw new Exception("Wrong docDate size for " + doc.getID());
    //				}
    //				int dateIndex = 0;
    //				for(DocumentDate dd : dates){
    //					String dateType = iDRegDocDateTypeMap.get(dd.getIDRegDocDateTypeID());
    //					DatePeriod dp =	dd.getDatePeriod();
    //				DatePointSchemaType dpst = dp.getStart().getFrom();
    //				Integer year = -1, day =-1, month = -1;
    //				BigInteger temp = dpst.getDay().getValue();
    //				Calendar cal = Calendar.getInstance();
    //				if (temp != null) {
    //					day = temp.intValue();
    //				}
    //				temp = dpst.getMonth().getValue();
    //				if (temp != null) {
    //					month = temp.intValue();
    //				}
    //				temp = dpst.getYear().getValue();
    //				if (temp != null) {
    //					year = temp.intValue();
    //				}
    //				if(year == -1 || month == -1 || day == -1){
    //					throw new Exception("Bad dates for " + doc.getID());
    //				}
    //				cal.set(year,  month, day);
    //				}
    //			}
    ArrayList<ScrapeIdentification> docs = docMap.get(doc.getIdentityID());
    if (docs == null) {
        docs = new ArrayList<ScrapeIdentification>();
        docMap.put(doc.getIdentityID(), docs);
    }
    docs.add(si);

    if (si.getValue().startsWith("IMO")) {
        ArrayList<String> alia = aliasMap.get(doc.getIdentityID());
        if (alia == null) {
            alia = new ArrayList<String>();
            aliasMap.put(doc.getIdentityID(), alia);
        }
        alia.add(si.getValue());
    }
}

//Addresses
LocPartTypeValues lptv = root.getReferenceValueSets().getLocPartTypeValues();
HashMap<BigInteger, String> lptvMap = new HashMap<BigInteger, String>();
for (LocPartType lpt : lptv.getLocPartType()) {
    lptvMap.put(lpt.getID(), lpt.getValue());
}
List<LocationSchemaType> locations = root.getLocations().getLocation();
List<AreaCode> areaCodes = root.getReferenceValueSets().getAreaCodeValues().getAreaCode();

areaMap = new HashMap<BigInteger, String>();
for (AreaCode act : areaCodes) {
    areaMap.put(act.getID(), act.getDescription());
}

addressMap = new HashMap<BigInteger, ScrapeAddress>();
for (LocationSchemaType lst : locations) {
    ScrapeAddress address = new ScrapeAddress();
    String addr1 = "";
    String addr2 = "";
    String addr3 = "";
    // make address
    for (LocationPart lp : lst.getLocationPart()) {
        for (LocationPartValue lpv : lp.getLocationPartValue()) {

            if (StringUtils.isBlank(lpv.getComment().getValue())) {

                String type = lptvMap.get(lp.getLocPartTypeID());
                if (StringUtils.isNotBlank(type)) {
                    if ("ADDRESS1".equals(type)) {
                        addr1 = StringUtils.isNotBlank(addr1) ? addr1 + " " + lpv.getValue().getValue() : lpv.getValue().getValue();
                    } else if ("ADDRESS2".equals(type)) {
                        addr2 = StringUtils.isNotBlank(addr2) ? addr2 + " " + lpv.getValue().getValue() : lpv.getValue().getValue();
                    } else if ("ADDRESS3".equals(type)) {
                        addr3 = StringUtils.isNotBlank(addr3) ? addr3 + " " + lpv.getValue().getValue() : lpv.getValue().getValue();
                    } else if ("CITY".equals(type)) {
                        address.setCity(lpv.getValue().getValue());
                    } else if ("STATE/PROVINCE".equals(type)) {
                        address.setProvince(lpv.getValue().getValue());
                    } else if ("POSTAL CODE".equals(type)) {
                        address.setPostalCode(lpv.getValue().getValue());
                    } else if ("Unknown".equals(type)) { //seems to only occur for nationality
                        address.setCountry(lpv.getValue().getValue());
                    }
                } else {
                    throw new Exception("Unmapped LocPartType: " + lp.getLocPartTypeID());
                }
            }
        }
    } // end for location parts

    if (lst.getLocationCountry().size() > 0) {
        BigInteger crid = lst.getLocationCountry().get(0).getCountryRelevanceID();
        //address.setType(countryRelevanceMap.get(crid)); //TODO remove -- just for testing
        String country = countryMap.get(lst.getLocationCountry().get(0).getCountryID()); // TODO get(0) ??
        if (StringUtils.isNotBlank(country)) {
            address.setCountry(country);
        }
    } else {
        //Address Remark
        if (lst.getLocationAreaCode() != null && lst.getLocationPart().size() > 0 && lst.getLocationPart().get(0).locPartTypeID == 1454
            && lst.getLocationAreaCode().get(0).areaCodeID != null && lst.getLocationAreaCode().get(0).areaCodeID == 11291) {
            addressRemark = "";
            for (LocationPart locationPart : lst.getLocationPart()) {
                String type = lptvMap.get(locationPart.getLocPartTypeID());
                for (LocationPartValue lpv : locationPart.getLocationPartValue()) {
                    if (StringUtils.isNotBlank(type)) {
                        if ("ADDRESS1".equals(type)) {
                            String address1 = StringUtils.isNotBlank(addr1) ? addr1 + " " + lpv.getValue() : lpv.getValue().getValue();
                            if (address1 != null) {
                                addressRemark = "ADDRESS1:" + address1 + ","
                            }
                        } else if ("ADDRESS2".equals(type)) {
                            String address2 = StringUtils.isNotBlank(addr2) ? addr2 + " " + lpv.getValue() : lpv.getValue().getValue();
                            if (address2 != null) {
                                addressRemark = addressRemark + " ADDRESS2:" + address2 + ","
                            }
                        } else if ("ADDRESS3".equals(type)) {
                            String address3 = StringUtils.isNotBlank(addr3) ? addr3 + " " + lpv.getValue() : lpv.getValue().getValue();
                            if (address3 != null) {
                                addressRemark = addressRemark + " ADDRESS3:" + address3 + ","
                            }
                        } else if ("CITY".equals(type)) {
                            String city = lpv.getValue().getValue();
                            if (city != null) {
                                addressRemark = addressRemark + " City:" + city + ","
                            }
                        } else if ("STATE/PROVINCE".equals(type)) {
                            String state = lpv.getValue().getValue();
                            if (state != null) {
                                addressRemark = addressRemark + " State:" + state + ","
                            }
                        } else if ("POSTAL CODE".equals(type)) {
                            String postalCode = lpv.getValue().getValue();
                            if (postalCode != null) {
                                addressRemark = addressRemark + " PostalCode:" + postalCode + ","
                            }
                        } else if ("REGION".equals(type)) {
                            String region = lpv.getValue().getValue();
                            if (region != null) {
                                addressRemark = addressRemark + " Region:" + region
                            }
                        }
                    }

                }
            }
            address.setRawFormat("Address: " + addressRemark);
        }
    }

    if (StringUtils.isBlank(address.getCountry()) && lst.getLocationAreaCode().size() > 0) {
        if (lst.getLocationAreaCode().size() > 1) {
            throw new Exception("Wrong areacode size: " + lst.getID());
        }
        String country = areaMap.get(lst.getLocationAreaCode().get(0).getAreaCodeID());
        if (country != null) {
            address.setCountry(country);
        } else {
            throw new Exception("Country did not map via areacode: " + lst.getID());
        }
    }

    if (StringUtils.isNotBlank(addr1) || StringUtils.isNotBlank(addr2) || StringUtils.isNotBlank(addr3)) {
        addr1 = addr1 + (StringUtils.isNotBlank(addr2) ? " " + addr2 : "");
        addr1 = addr1 + (StringUtils.isNotBlank(addr3) ? " " + addr3 : "");
        def postalCode;
        if (address.getCountry().toString() == "Iran" || address.getCountry().toString().equalsIgnoreCase("Iran")) {
            def postalCodeMatch = addr1 =~ /, (\d{10})$/
            if (postalCodeMatch.find()) {
                postalCode = postalCodeMatch[0][1];
            }
        }

        if (address.getCountry().toString() == "Germany" || address.getCountry().toString().equalsIgnoreCase("Germany")) {
            def postalCodeMatch = addr1 =~ /, (\d{5})$/
            if (postalCodeMatch.find()) {
                postalCode = postalCodeMatch[0][1];
            }
        }

        if (postalCode) {
            address.setPostalCode(postalCode);
            addr1 = addr1.replaceAll(postalCode, "").trim().replaceAll(/,$/, "");
        }
        address.setAddress1(addr1);
    }
    // add to hashmap
    addressMap.put(lst.getID(), address);
} //end for locations

List<DistinctPartySchemaType> parties = root.getDistinctParties().getDistinctParty();
List<ScrapeEntity> retList = new ArrayList<ScrapeEntity>(parties.size());

entityMap = new HashMap<String, ScrapeEntity>();
for (DistinctPartySchemaType dpst : parties) {
    ScrapeEntity entity = context.session.newEntity();

    entity.setId(dpst.getFixedRef() + "");
    entity.setDataSourceId(dpst.getFixedRef() + "");
    ScrapeIdentification si = new ScrapeIdentification();
    si.setType("OFAC UID");
    si.setValue(dpst.getFixedRef());
    entity.addIdentification(si);

    List<Comment> comments = dpst.getComment();
    for (Comment comment : comments) {
        if (StringUtils.isNotBlank(comment.getValue().trim())) {
            entity.addRemark(comment.getValue().trim());
        }
    }

    if (individualSubTypeIds.contains(dpst.getProfile().get(0).getPartySubTypeID())) {
        entity.setType("P");
    } else {
        entity.setType("O");
    }
    //			PartySubType pst = partySubTypeMap.get(dpst.getProfile().get(0).getPartySubTypeID());

    processProfiles(dpst.getProfile(), entity);

    //	System.out.println(sest);
    //	retList.add(entity);
    entityMap.put(dpst.getFixedRef(), entity);
}

//add relationships
relationTypeMap = new HashMap<BigInteger, String>();
for (RelationType rt : root.getReferenceValueSets().getRelationTypeValues().getRelationType()) {
    relationTypeMap.put(rt.getID(), rt.getValue());
}

ScrapeEntityAssociation scrapeEntityAssociation;
for (ProfileRelationshipSchemaType prst : root.getProfileRelationships().getProfileRelationship()) {
    BigInteger fromID = prst.getFromProfileID();
    BigInteger toID = prst.getToProfileID();
    String type = relationTypeMap.get(prst.getRelationTypeID());
    ScrapeEntity fromEntity = entityMap.get(fromID + "");
    if (fromEntity == null) {
        throw new Exception("From entity not found for relationship " + prst.getID());
    }
    ScrapeEntity toEntity = entityMap.get(toID + "");
    if (toEntity == null) {
        throw new Exception("To entity not found for relationship " + prst.getID());
    }
    scrapeEntityAssociation = new ScrapeEntityAssociation(toID + "");
    convertRelationShip(scrapeEntityAssociation, prst.getRelationTypeID(), relationTypeMap);

    scrapeEntityAssociation.setHashable(toEntity.getName(), toEntity.getType());
    fromEntity.addScrapeEntityAssociation(scrapeEntityAssociation);
}

if (featureList.size() > 0) {
    StringBuilder sb = new StringBuilder();
    sb.append("\nFeatures not yet processed:\n");
    for (String feat : featureList) {
        sb.append(feat + "\n");
    }
    context.info(sb.toString());
}
//	return new ArrayList(entityMap.values());


private void convertRelationShip(ScrapeEntityAssociation assc, BigInteger value, Map<BigInteger, String> relMap) throws Exception {
    String relTypeTemp = relMap.get(value);
    RelationshipType relType = null;

    if ("Associate Of".equals(relTypeTemp)) {
        relType = RelationshipType.ASSOCIATE;
    } else if ("Providing support to".equals(relTypeTemp)) {
        relType = RelationshipType.ASSOCIATE;
    } else if ("Acting for or on behalf of".equals(relTypeTemp)) {
        relType = RelationshipType.AGENT_REPRESENTATIVE;
    } else if ("Owned or Controlled By".equals(relTypeTemp)) {
        relType = RelationshipType.ASSOCIATE;
    } else if ("Family member of".equals(relTypeTemp)) {
        relType = RelationshipType.FAMILY_MEMBER;
    } else if ("playing a significant role in".equals(relTypeTemp)) {
        relType = RelationshipType.ASSOCIATE;
    } else if ("Leader or official of".equals(relTypeTemp)) {
        relType = RelationshipType.ASSOCIATE;
    } else if ("Principal Executive Officer".equals(relTypeTemp)) {
        relType = RelationshipType.ASSOCIATE;
    } else if ("Owns, controls, or operates".equals(relTypeTemp)) {
        relType = RelationshipType.ASSOCIATE;
    }else if ("Property in the interest of".equals(relTypeTemp)) {
        relType = RelationshipType.ASSOCIATE;
    }
    if (relType == null) {
        relType = RelationshipType.ASSOCIATE;
        //throw new Exception("OfacSdnFeedDeserializer. Could not find relationship type mapping for " + value);
    }
    assc.setRelationshipType(relType);
}

private void processProfiles(List<Profile> profiles, ScrapeEntity entity) throws Exception {
    for (Profile prof : profiles) {
        processIdentities(prof.getIdentity(), entity);
        processSanctions(prof.getID(), entity);
        processFeatures(prof.getFeature(), entity);
        if (entity.getType().equals("P")) {
            List<ScrapeEvent> events = entity.getEvents();
            for (ScrapeEvent event : events) {
                if (event.getDescription().startsWith("This entity appears on the United States Treasury Department")) {
                    event.setDescription(event.getDescription().replace("This entity appears on the United States Treasury Department", "This individual appears on the United States Treasury Department"));
                }
            }
        }
        if (prof.getIdentity().size() > 1) {
            throw new Exception("Wrong size for identity for profile " + prof.getID());
        }
    }
}

def getRange(one, two) {
    int min = new Integer(one.replaceAll(/-\d\d*-\d\d*/, ''));
    int max = new Integer(two.replaceAll(/-\d\d*-\d\d*/, ''));
    if (min > max) {
        min = new Integer(two.replaceAll(/-\d\d*-\d\d*/, ''));
        max = new Integer(one.replaceAll(/-\d\d*-\d\d*/, ''));
    }
    retVal = [];
    for (int i = min + 1; i <= max - 1; i++) {
        retVal.add(i);
    }
    retVal.add(one.replaceAll(/(\d{4})-(\d\d*)-(\d\d*)/, '$2/$3/$1'))
    retVal.add(two.replaceAll(/(\d{4})-(\d\d*)-(\d\d*)/, '$2/$3/$1'))
    return retVal;
}

private void processFeatures(List<FeatureSchemaType> fsts, ScrapeEntity entity) throws Exception {
    for (FeatureSchemaType fst : fsts) {
        //System.out.println("Feature:" + fst.getID());
        //			String reliability = reliabilityMap.get(fst.getFeatureVersion().get(0).getReliabilityID());

        if (fst.getFeatureVersion().size() > 1) {
            throw new Exception("Feature version bad size: " + fst.getID());
        }
        if (fst.getFeatureVersion().get(0).getVersionLocation().size() > 1) {
            throw new Exception("Feature version location bad size: " + fst.getID());
        }
        String featureType = featureTypeHash.get(fst.getFeatureTypeID());

        if (StringUtils.isNotBlank(featureType)) {
            // address
            if ("Place of Birth".equals(featureType)) {
                // ScrapeAddress fromMap =
                // addressMap.get(fst.getFeatureVersion().get(0).getVersionLocation().get(0).getLocationID());
                String[] splitUp = fst.getFeatureVersion().get(0).getVersionDetail().get(0).getValue().split(",");
                if (splitUp != null && splitUp.length > 0) {
                    ScrapeAddress newAddress = new ScrapeAddress();
                    newAddress.setBirthPlace(true);
                    if (splitUp.length > 2) {
                        newAddress.setCity(splitUp[0].trim());
                        newAddress.setProvince(splitUp[1].trim());
                        newAddress.setCountry(splitUp[splitUp.length - 1].trim());
                    } else if (splitUp.length > 1) {
                        newAddress.setCity(splitUp[0].trim());
                        newAddress.setCountry(splitUp[1].trim());
                    } else {
                        newAddress.setCountry(splitUp[0]);
                    }
                    entity.addAddress(newAddress);
                }
            } else if ("Nationality Country".equals(featureType)) {
                ScrapeAddress fromMap = addressMap.get(fst.getFeatureVersion().get(0).getVersionLocation().get(0).getLocationID());
                entity.addNationality(fromMap.getCountry());
            } else if ("Citizenship Country".equals(featureType)) {
                ScrapeAddress fromMap = addressMap.get(fst.getFeatureVersion().get(0).getVersionLocation().get(0).getLocationID());
                entity.addCitizenship(fromMap.getCountry());
            } else if ("Location".equals(featureType)) {
                ScrapeAddress fromMap = addressMap.get(fst.getFeatureVersion().get(0).getVersionLocation().get(0).getLocationID());
                ScrapeAddress newAddress = new ScrapeAddress(fromMap);
                newAddress.setBirthPlace(false);
                if (!newAddress.getCountry().equals("undetermined")) {
                    entity.addAddress(newAddress);
                } else {
                    if (newAddress.getRawFormat() != null) {
                        entity.addRemark(newAddress.getRawFormat());
                        newAddress.setRawFormat("");
                    }
                }
            } else if ("Former Citizenship Country".equals(featureType)) {
                ScrapeAddress fromMap = addressMap.get(fst.getFeatureVersion().get(0).getVersionLocation().get(0).getLocationID());
                ScrapeAddress newAddress = new ScrapeAddress(fromMap);
                newAddress.setBirthPlace(false);
                entity.addAddress(newAddress);
            }
            // birthdate
            else if ("Birthdate".equals(featureType)) {
                ScrapeDob dob = new ScrapeDob();
                List<DatePeriod> dp = fst.getFeatureVersion().get(0).getDatePeriod();
                if (dp.size() != 1) {
                    throw new Exception("DatePeriod wrong size: " + fst.getID());
                }
                DatePointSchemaType dpst = dp.get(0).getStart().getFrom();
                DatePointSchemaType dpst2 = dp.get(0).getEnd().getFrom();
                BigInteger temp = dpst.getDay().getValue();
                if (dpst.getDay().getValue().equals(_1) &&
                    dpst.getMonth().getValue().equals(_1) &&
                    dpst2.getDay().getValue().equals(_31) &&
                    dpst2.getMonth().getValue().equals(_12) &&
                    dpst.getYear().getValue().equals(dpst2.getYear().getValue())) {
                    dob.setYear(dpst.getYear().getValue().toString());
                    dob.setCirca(dp.get(0).getStart().isApproximate());
                    entity.addDateOfBirth(dob);
                } else if (dpst.getDay().getValue().equals(_1) &&
                    dpst.getMonth().getValue().equals(_1) &&
                    ((dpst2.getDay().getValue().equals(_1) && dpst2.getMonth().getValue().equals(_1))
                        || (dpst2.getDay().getValue().equals(_31) && dpst2.getMonth().getValue().equals(_12))) &&
                    !dpst.getYear().getValue().equals(dpst2.getYear().getValue())) {
                    range = getRange(dpst.getYear().getValue().toString(), dpst2.getYear().getValue().toString());
                    range.each { dateInRange ->
                        def myDob;
                        if (dateInRange instanceof String && dateInRange.contains("/")) {
                            myDob = new ScrapeDob(dateInRange);
                        } else {
                            myDob = new ScrapeDob("-/-/" + dateInRange);
                        }
                        myDob.setCirca(dp.get(0).getStart().isApproximate());
                        entity.addDateOfBirth(myDob);
                    }
                } else {
                    if (temp != null) {
                        dob.setDay(temp.toString());
                    }
                    temp = dpst.getMonth().getValue();
                    if (temp != null) {
                        dob.setMonth(temp.toString());
                    }
                    temp = dpst.getYear().getValue();
                    if (temp != null) {
                        dob.setYear(temp.toString());
                    }

                    dob.setCirca(dp.get(0).getStart().isApproximate());
                    entity.addDateOfBirth(dob);

                }
            }
            //gender
            else if ("Gender".equals(featureType)) {
                if (fst.getFeatureVersion().get(0).getVersionDetail().size() != 1) {
                    throw new Exception("Version Detail wrong size: " + fst.getID());
                }
                String sex = detailReferenceMap.get(fst.getFeatureVersion().get(0).getVersionDetail().get(0).getDetailReferenceID());
                if (StringUtils.isNotBlank(sex)) {
                    entity.addSex(sex);
                } else {
                    throw new Exception("Gender was blank: " + fst.getID());
                }
            }
            //website
            else if ("Website".equals(featureType)) {
                if (fst.getFeatureVersion().get(0).getVersionDetail().size() != 1) {
                    throw new Exception("Version Detail wrong size: " + fst.getID());
                }
                //String url = fst.getFeatureVersion().get(0).getVersionDetail().get(0).getValue();
                for (FeatureSchemaType.FeatureVersion fv : fst.getFeatureVersion()) {
                    for (VersionDetail vd : fv.getVersionDetail()) {
                        String url = vd.getValue();
                        if (StringUtils.isNotBlank(url)) {
                            processDetailToIdentification("Website", fst, entity);
                            //entity.addUrl(url);
                        } else {
                            throw new Exception("Url was blank: " + fst.getID());
                        }
                    }
                }

            }
            //sanctions
            //docs
            //previous citizenship
            //email address
            else if ("Email Address".equals(featureType)) {
                processDetailToIdentification("Email", fst, entity);
            } else if ("D-U-N-S Number".equals(featureType)) {
                processDetailToIdentification("D-U-N-S Number", fst, entity);
            }
            //Title (Position)
            else if ("Title".equals(featureType)) {
                if (fst.getFeatureVersion().get(0).getVersionDetail().size() != 1) {
                    throw new Exception("Version Detail wrong size: " + fst.getID());
                }

                for (FeatureSchemaType.FeatureVersion fv : fst.getFeatureVersion()) {
                    for (VersionDetail vd : fv.getVersionDetail()) {
                        String position = vd.getValue();
                        if (StringUtils.isNotBlank(position)) {
                            entity.addPosition(position);
                        } else {
                            throw new Exception("Position was blank: " + fst.getID());
                        }
                    }
                }
            }
            //Vessel Owner
            else if ("Vessel Owner".equals(featureType)) {
                processDetailToRemark("Vessel Owner", fst, entity);
            }
            //Vessel Flag
            else if ("Vessel Flag".equals(featureType)) {
                processDetailToRemark("Vessel Flag", fst, entity);
            }
            //Former Vessel Flag
            else if ("Former Vessel Flag".equals(featureType)) {
                processDetailToRemark("Former Vessel Flag", fst, entity);
            }
            //Vessel Call Sign
            else if ("Vessel Call Sign".equals(featureType)) {
                processDetailToIdentification("Vessel Call Sign", fst, entity);
            }
            //Vessel Gross Registered Tonnage
            else if ("Vessel Gross Registered Tonnage".equals(featureType)) {
                processDetailToRemark("Vessel Gross Registered Tonnage", fst, entity);
            }
            //Vessel Tonnage
            else if ("Vessel Tonnage".equals(featureType)) {
                processDetailToRemark("Vessel Tonnage", fst, entity);
            }
            //Former Vessel Flag
            else if ("VESSEL TYPE".equals(featureType)) {
                processDetailReferenceToRemark("Vessel Type", fst, entity);
                //processDetailToRemark("Vessel Type", fst, entity);
                boolean found = false;
                List<ScrapeEvent> events = entity.getEvents();
                for (ScrapeEvent event : events) {
                    if (event.getDescription().startsWith("This entity appears on the United States Treasury Department")) {
                        found = true;
                        event.setDescription(event.getDescription().replace("This entity appears on the United States Treasury Department", "This vessel appears on the United States Treasury Department"));
                    }
                }
                if (!found) {
                    ScrapeEvent event = new ScrapeEvent();
                    event.setDescription("Vessel appears on OFAC SDN list");
                    entity.addEvent(event);
                }
            }
            //Aircraft Operator
            else if ("Aircraft Operator".equals(featureType)) {
                processDetailToRemark("Aircraft Operator", fst, entity);
            }
            //Aircraft Model
            else if ("Aircraft Model".equals(featureType)) {
                processDetailToRemark("Aircraft Model", fst, entity);
                List<ScrapeEvent> events = entity.getEvents();
                boolean found = false;
                for (ScrapeEvent event : events) {
                    if (event.getDescription().startsWith("This entity appears on the United States Treasury Department")) {
                        found = true;
                        event.setDescription(event.getDescription().replace("This entity appears on the United States Treasury Department", "This aircraft appears on the United States Treasury Department"));
                    }
                }
                if (!found) {
                    ScrapeEvent event = new ScrapeEvent();
                    event.setDescription("Aircraft appears on OFAC SDN list");
                    entity.addEvent(event);
                }
            }
            //Aircraft Model
            else if ("UN/LOCODE".equals(featureType)) {
                processDetailToIdentification("UN/LOCODE", fst, entity);
            }
            //Aircraft Manufacturers Serial Number (MSN)
            else if ("Aircraft Manufacturers Serial Number (MSN)".equals(featureType)) {
                processDetailToIdentification("Aircraft Manufacturers Serial Number (MSN)", fst, entity);
            }
            //Aircraft Manufacturer's Serial Number (MSN)
            else if ("Aircraft Manufacturer\u2019s Serial Number (MSN)".equals(featureType)) {
                processDetailToIdentification("Aircraft Manufacturer\u2019s Serial Number (MSN)", fst, entity);
            }
            //Aircraft Tail Number
            else if ("Aircraft Tail Number".equals(featureType)) {
                processDetailToIdentification("Aircraft Tail Number", fst, entity);
            }
            //Previous Aircraft Tail Number
            else if ("Previous Aircraft Tail Number".equals(featureType)) {
                processDetailToIdentification("Previous Aircraft Tail Number", fst, entity);
            }
            //Aircraft Construction Number (also called L/N or S/N or F/N)
            else if ("Aircraft Construction Number (also called L/N or S/N or F/N)".equals(featureType)) {
                processDetailToIdentification("Aircraft Construction Number(L/N, S/N, or F/N)", fst, entity);
            }
            //Aircraft Manufacture Date
            else if ("Aircraft Manufacture Date".equals(featureType)) {
                List<DatePeriod> dp = fst.getFeatureVersion().get(0).getDatePeriod();
                if (dp.size() != 1) {
                    throw new Exception("DatePeriod wrong size: " + fst.getID());
                }
                DatePointSchemaType dpst = dp.get(0).getStart().getFrom();
                Integer year = -1, day = -1, month = -1;
                BigInteger temp = dpst.getDay().getValue();
                if (temp != null) {
                    day = temp.intValue();
                }
                temp = dpst.getMonth().getValue();
                if (temp != null) {
                    month = temp.intValue();
                }
                temp = dpst.getYear().getValue();
                if (temp != null) {
                    year = temp.intValue();
                }
                if (year == -1 || month == -1 || day == -1) {
                    throw new Exception("Bad dates for " + fst.getID());
                }
                entity.addRemark("Aircraft Manufacture Date: " + month + "/" + day + "/" + year);
            }
            //BIK (RU)
            else if ("BIK (RU)".equals(featureType)) {
                processDetailToIdentification("BIK (RU)", fst, entity);
            }
            //BIC Container Code
            else if ("BIC Container Code".equals(featureType)) {
                processDetailToIdentification("BIC Container Code", fst, entity);
            }
            //SWIFT/BIC
            else if ("SWIFT/BIC".equals(featureType)) {
                processDetailToIdentification("SWIFT/BIC", fst, entity);
            }
            //Additional Sanctions Information -
            else if (featureType.indexOf("Additional Sanctions Information") >= 0) {
                processDetailReferenceToRemark("Additional Sanctions Information", fst, entity);
            }
            //Secondary Sanctions Information -
            else if ("Secondary sanctions risk:".equals(featureType)) {
                processDetailReferenceToRemark("Secondary Sanctions Information", fst, entity);
            } else if ("Transactions Prohibited For Persons Owned or Controlled By U.S. Financial Institutions:".equals(featureType)) {
                processDetailReferenceToRemark("Transactions Prohibited For Persons Owned or Controlled By U.S. Financial Institutions", fst, entity);
            }
            //Executive Order 13645 Determination -
            else if ("Executive Order 13645 Determination - ".equals(featureType)) {
                processDetailReferenceToRemark("Executive Order 13645 Determination", fst, entity);
            }
            //IFCA Determination -
            else if (featureType.indexOf("IFCA Determination") >= 0) {
                processDetailReferenceToRemark("IFCA Determination", fst, entity);
            }
            //MICEX Code
            else if ("MICEX Code".equals(featureType)) {
                processDetailToIdentification("MICEX Code", fst, entity);
            }
            //Digital Currency Address
            else if (featureType.indexOf("Digital Currency Address") >= 0) {
                String digitalCurrencyRemark = "Digital Currency Address - " + featureType[-3..-1]
                processDetailToIdentification(digitalCurrencyRemark, fst, entity);
            } else if (featureType.indexOf("Organization Established Date") >= 0) {
                processOrgEstablishedDate(entity, fst)
            } else if (featureType.indexOf("Target Type") >= 0) {
                processDetailReferenceToRemarkNoColon("Target Type", fst, entity);
            } else if (featureType.indexOf("Organization Type") >= 0) {
                processDetailReferenceToRemarkWithSpace("Organization Type", fst, entity);
            }

            else {
                featureList.add(featureType);
                //throw new Exception("Feature type  not mapped: " + fst.getFeatureTypeID());
            }


        } // end is not blank feature type
    }
}

private void processOrgEstablishedDate(ScrapeEntity entity, FeatureSchemaType fst) {
    /*if (entity.getName().contains("ALFA-BANK")) {
        print "STOP";
    }*/

    //get Start Date
    List<DatePeriod> dp = fst.getFeatureVersion().get(0).getDatePeriod();
    if (dp.size() != 1) {
        throw new Exception("DatePeriod wrong size: " + fst.getID());
    }
    DatePointSchemaType dpst = dp.get(0).getStart().getFrom();
    Integer year = -1, day = -1, month = -1;
    BigInteger temp = dpst.getDay().getValue();
    if (temp != null) {
        day = temp.intValue();
    }
    temp = dpst.getMonth().getValue();
    if (temp != null) {
        month = temp.intValue();
    }
    temp = dpst.getYear().getValue();
    if (temp != null) {
        year = temp.intValue();
    }
    if (year == -1 || month == -1 || day == -1) {
        throw new Exception("Bad dates for " + fst.getID());
    }

    //get EndDate
    DatePointSchemaType endDpst = dp.get(0).getEnd().getFrom();
    Integer endYear = -1, endDay = -1, endMonth = -1;
    BigInteger endTemp = endDpst.getDay().getValue();
    if (endTemp != null) {
        endDay = endTemp.intValue();
    }
    endTemp = endDpst.getMonth().getValue();
    if (endTemp != null) {
        endMonth = endTemp.intValue();
    }
    endTemp = endDpst.getYear().getValue();
    if (endTemp != null) {
        endYear = endTemp.intValue();
    }
    if (endYear == -1 || endMonth == -1 || endDay == -1) {
        throw new Exception("Bad dates for " + fst.getID());
    }


    //if start and end dates span the whole year, we just need year.
    if (day == 1 && endDay == 31 && month == 1 && endMonth == 12 && year == endYear) {
        month = null;
    }

    def SHORT_MONTHS = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"]
    if (month) {
        entity.addRemark("Organization Established Date " + (day+"").padLeft(2,'0') + " " + SHORT_MONTHS[month - 1] + " " + year);
    } else {
        entity.addRemark("Organization Established Date " + year);
    }
}

private void processDoc(BigInteger id, ScrapeEntity entity) {
    List<ScrapeIdentification> temp = docMap.get(id);
    if (temp != null) {
        for (ScrapeIdentification identification : temp) {
            entity.addIdentification(identification);
        }
    }
    List<String> temp2 = aliasMap.get(id);
    if (temp2 != null) {
        for (String str : temp2) {
            addAlias(entity, str); //these are imo only
        }
    }
}

def addAlias(entityIn, aliasIn) {
    def theAlia = aliasIn.split(/;\s.*?:/);
    theAlia.each { aliasInNew ->
        aliasInNew = aliasInNew.replaceAll(/;(_SEP|$)/, '$1');

        splitUpAlias = aliasInNew.split("_SEP_")
        if (splitUpAlias.length != 2 || (splitUpAlias.length == 2 && splitUpAlias[1] == "215")) {
            entityIn.addAlias(splitUpAlias[0])
        } else {
            ScrapeAlias sa = new ScrapeAlias();
            sa.setName(splitUpAlias[0]);
            sa.setType(com.rdc.rdcmodel.model.AliasType.AKA);
            if (splitUpAlias.length == 2) {
                if (splitUpAlias[1] == '160') {
                    sa.setScript('Arab');
                } else if (splitUpAlias[1] == '220') {
                    sa.setScript('Cyrl');
                } else {
                    tempScript = determineScript(sa.getName());
                    /* println 'for name ' + sa.getName() + " script was " + tempScript;
                     if(tempScript.equals('Hani')){
                         print "Script found"
                     }*/
                    if (tempScript) {
                        sa.setScript(tempScript.split(",")[0])
                    }
                }
            }
            entityIn.addDetailedAlias(sa);
        }
    }
}

def determineScript(input) {
    //println 'determing script'
    script = '';
    Character.UnicodeScript.each {
        if (it.toString() == 'COMMON') {
            return
        }
        if (containsScriptLetters(input, it)) {
            if (!binding.variables.containsKey("scriptMap")) {
                //println 'prepping map'
                prepScriptMap()
            }
            theScript = scriptMap[it.toString()]
            if (theScript) {
                script += (script.length() > 0 ? ", " : "") + theScript;
            }
        }
    }
    return script;
}

def containsScriptLetters(text, script) {
    retVal = false;
    for (int i = 0; i < text.length();) {
        int codepoint = text.codePointAt(i);
        i += Character.charCount(codepoint);
        if (Character.UnicodeScript.of(codepoint) == script) {
            retVal = true;
        }
    }
    return retVal;
}

private void processSanctions(BigInteger id, ScrapeEntity entity) {
    List<ScrapeEvent> temp = sanctionsMap.get(id);
    if (temp != null) {
        for (ScrapeEvent event : temp) {
            entity.addEvent(event);
        }
    }
}

private void processDetailToRemark(String title, FeatureSchemaType fst, ScrapeEntity entity) throws Exception {
    if (fst.getFeatureVersion().get(0).getVersionDetail().size() != 1) {
        throw new Exception("Version Detail wrong size: " + fst.getID());
    }

    for (FeatureSchemaType.FeatureVersion fv : fst.getFeatureVersion()) {
        for (VersionDetail vd : fv.getVersionDetail()) {
            String model = vd.getValue();

            if (StringUtils.isNotBlank(model)) {
                entity.addRemark(title + ":" + model);
                if ("Vessel Flag".equals(title)) {
                    ScrapeAddress addr = new ScrapeAddress();
                    addr.setCountry(model);
                    entity.addAddress(addr);
                }
            } else {
                throw new Exception(title + " was empty for " + fst.getID());
            }
        }
    }
}

private void processDetailReferenceToRemark(String title, FeatureSchemaType fst, ScrapeEntity entity) throws Exception {
    if (fst.getFeatureVersion().get(0).getVersionDetail().size() != 1) {
        throw new Exception("Version Detail wrong size: " + fst.getID());
    }

    for (FeatureSchemaType.FeatureVersion fv : fst.getFeatureVersion()) {
        for (VersionDetail vd : fv.getVersionDetail()) {
            String model = detailReferenceMap.get(vd.getDetailReferenceID());

            if (StringUtils.isNotBlank(model)) {
                entity.addRemark(title + ":" + model);
            } else {
                throw new Exception(title + " was empty for fst " + fst.getID() + ", fv " + fv.getID() + ", vd " + vd.getDetailTypeID() + ", vd ref " + vd.getDetailReferenceID());
            }
        }
    }
}

private void processDetailReferenceToRemarkWithSpace(String title, FeatureSchemaType fst, ScrapeEntity entity) throws Exception {
    if (fst.getFeatureVersion().get(0).getVersionDetail().size() != 1) {
        throw new Exception("Version Detail wrong size: " + fst.getID());
    }

    for (FeatureSchemaType.FeatureVersion fv : fst.getFeatureVersion()) {
        for (VersionDetail vd : fv.getVersionDetail()) {
            String model = detailReferenceMap.get(vd.getDetailReferenceID());

            if (StringUtils.isNotBlank(model)) {
                entity.addRemark(title + ": " + model);
            } else {
                throw new Exception(title + " was empty for fst " + fst.getID() + ", fv " + fv.getID() + ", vd " + vd.getDetailTypeID() + ", vd ref " + vd.getDetailReferenceID());
            }
        }
    }
}


private void processDetailReferenceToRemarkNoColon(String title, FeatureSchemaType fst, ScrapeEntity entity) throws Exception {
    if (fst.getFeatureVersion().get(0).getVersionDetail().size() != 1) {
        throw new Exception("Version Detail wrong size: " + fst.getID());
    }

    for (FeatureSchemaType.FeatureVersion fv : fst.getFeatureVersion()) {
        for (VersionDetail vd : fv.getVersionDetail()) {
            String model = detailReferenceMap.get(vd.getDetailReferenceID());

            if (StringUtils.isNotBlank(model)) {
                entity.addRemark(title + " " + model);
            } else {
                throw new Exception(title + " was empty for fst " + fst.getID() + ", fv " + fv.getID() + ", vd " + vd.getDetailTypeID() + ", vd ref " + vd.getDetailReferenceID());
            }
        }
    }
}



private void processDetailToIdentification(String title, FeatureSchemaType fst, ScrapeEntity entity) throws Exception {
    if (fst.getFeatureVersion().get(0).getVersionDetail().size() != 1) {
        throw new Exception("Version Detail wrong size: " + fst.getID());
    }

    for (FeatureSchemaType.FeatureVersion fv : fst.getFeatureVersion()) {
        for (VersionDetail vd : fv.getVersionDetail()) {
            String model = vd.getValue();

            if (StringUtils.isNotBlank(model)) {
                ScrapeIdentification si = new ScrapeIdentification();
                si.setType(title);
                si.setValue(model);
                entity.addIdentification(si);
            } else {
                throw new Exception(title + " was empty for " + fst.getID());
            }
        }
    }
}

private void processIdentities(List<IdentitySchemaType> identities, ScrapeEntity entity) {
    for (IdentitySchemaType ident : identities) {
        HashMap<BigInteger, String> mpgMap = mapMasterNamePartGroup(ident.getNamePartGroups().getMasterNamePartGroup());

        processDoc(ident.getID(), entity);

        List<Alias> alia = ident.getAlias();
        for (Alias ali : alia) {
            for (int i = 0; i < ali.getDocumentedName().size(); i++) {
                if (ident.isPrimary() && ali.getAliasTypeID().equals(aliasNameTypeId) && ali.getDocumentedName().get(i).getDocNameStatusID().equals(docNameStatusID)) {
                    entity.setName(processDocumentedName(ali.getDocumentedName().get(i), mpgMap, true));
                } else {
                    addAlias(entity, processDocumentedName(ali.getDocumentedName().get(i), mpgMap, false, true));
                }
            }
        }
    }
}

private HashMap<BigInteger, String> mapMasterNamePartGroup(List<MasterNamePartGroup> npg) {
    HashMap<BigInteger, String> retMap = new HashMap<BigInteger, String>();
    for (MasterNamePartGroup mnpg : npg) {
        retMap.put(mnpg.getNamePartGroup().get(0).getID(), namePartTypeMap.get(mnpg.getNamePartGroup().get(0).getNamePartTypeID()));
    }
    return retMap;
}

private String processDocumentedName(DocumentedNameSchemaType dnst, HashMap<BigInteger, String> map, boolean mainName, boolean appendScript = false) {
    int size = dnst.getDocumentedNamePart().size();
    PersonName pn = new PersonName();
    String retVal = "";
    boolean hasValidLast = false;
    String scriptId;
    for (int i = 0; i < size; i++) {
        NamePartValue npv = dnst.getDocumentedNamePart().get(i).getNamePartValue();
        String tempType = map.get(npv.getNamePartGroupID());
        scriptId = npv.getScriptID();
        /*eNames = ['Erken', 'Tuniyaz']
        if(eNames.contains(npv.getValue())) {
            println "Found"
        }*/
        if ("Last Name".equals(tempType)) {
            pn.setLastname(StringUtils.isBlank(pn.getLastname()) ? npv.getValue() : pn.getLastname() + " " + npv.getValue());
            hasValidLast = true;
        } else if ("Patronymic".equals(tempType) || "Matronymic".equals(tempType)) {
            pn.setLastname(StringUtils.isBlank(pn.getLastname()) ? npv.getValue() : (hasValidLast ? npv.getValue() + " " + pn.getLastname() : pn.getLastname() + " " + npv.getValue()));
        } else if ("First Name".equals(tempType)) {
            pn.setFirstname(StringUtils.isBlank(pn.getFirstname()) ? npv.getValue() : pn.getFirstname() + " " + npv.getValue());
        } else if ("Middle Name".equals(tempType)) {
            pn.setMiddlename(StringUtils.isBlank(pn.getMiddlename()) ? npv.getValue() : pn.getMiddlename() + " " + npv.getValue());
        } else if ("Maiden Name".equals(tempType)) {
            pn.setMiddlename(StringUtils.isBlank(pn.getMiddlename()) ? npv.getValue() : pn.getMiddlename() + " " + npv.getValue());
        } else {
            retVal = StringUtils.isBlank(retVal) ? npv.getValue() : retVal + "; " + tempType + ":" + npv.getValue() + ";";
        }
    }
    retVal = StringUtils.isBlank(retVal) ? personNameParser.formatName(pn) : retVal;
    retVal = retVal.replaceAll(/(?i)^(.*?)(,\s+(?:JR\.*|SR\.*|II|III))\s+(.*)$/, '$1 $3$2').replaceAll(/(?i)^((?:JR\.*|SR\.*|II|III)),\s+(.*?)\s+(.*)$/, '$2 $3, $1').replaceAll(/\s{2,}/, ' ');
    //context.info("retVal: " + retVal);
    return retVal + (appendScript ? (scriptId != null ? "_SEP_" + scriptId : "") : "");
}

def prepScriptMap() {
    scriptMap = [:];
    scriptMap['ADLAM'] = 'Adlm'
    scriptMap['CAUCASIAN_ALBANIAN'] = 'Aghb'
    scriptMap['AHOM'] = 'Ahom'
    scriptMap['ARABIC'] = 'Arab'
    scriptMap['IMPERIAL_ARAMAIC'] = 'Armi'
    scriptMap['ARMENIAN'] = 'Armn'
    scriptMap['AVESTAN'] = 'Avst'
    scriptMap['BALINESE'] = 'Bali'
    scriptMap['BAMUM'] = 'Bamu'
    scriptMap['BASSA_VAH'] = 'Bass'
    scriptMap['BATAK'] = 'Batk'
    scriptMap['BENGALI'] = 'Beng'
    scriptMap['BHAIKSUKI'] = 'Bhks'
    scriptMap['BOPOMOFO'] = 'Bopo'
    scriptMap['BRAHMI'] = 'Brah'
    scriptMap['BRAILLE'] = 'Brai'
    scriptMap['BUGINESE'] = 'Bugi'
    scriptMap['BUHID'] = 'Buhd'
    scriptMap['CHAKMA'] = 'Cakm'
    scriptMap['CANADIAN_ABORIGINAL'] = 'Cans'
    scriptMap['CARIAN'] = 'Cari'
    scriptMap['CHAM'] = 'Cham'
    scriptMap['CHEROKEE'] = 'Cher'
    scriptMap['COPTIC'] = 'Copt'
    scriptMap['CYPRIOT'] = 'Cprt'
    scriptMap['CYRILLIC'] = 'Cyrl'
    scriptMap['DEVANAGARI'] = 'Deva'
    scriptMap['DESERET'] = 'Dsrt'
    scriptMap['DUPLOYAN'] = 'Dupl'
    scriptMap['EGYPTIAN_HIEROGLYPHS'] = 'Egyp'
    scriptMap['ELBASAN'] = 'Elba'
    scriptMap['ETHIOPIC'] = 'Ethi'
    scriptMap['GEORGIAN'] = 'Geor'
    scriptMap['GLAGOLITIC'] = 'Glag'
    scriptMap['GOTHIC'] = 'Goth'
    scriptMap['GRANTHA'] = 'Gran'
    scriptMap['GREEK'] = 'Grek'
    scriptMap['GUJARATI'] = 'Gujr'
    scriptMap['GURMUKHI'] = 'Guru'
    scriptMap['HANGUL'] = 'Hang'
    scriptMap['HAN'] = 'Hani'
    scriptMap['HANUNOO'] = 'Hano'
    scriptMap['HATRAN'] = 'Hatr'
    scriptMap['HEBREW'] = 'Hebr'
    scriptMap['HIRAGANA'] = 'Hira'
    scriptMap['ANATOLIAN_HIEROGLYPHS'] = 'Hluw'
    scriptMap['PAHAWH_HMONG'] = 'Hmng'
    scriptMap['KATAKANA_OR_HIRAGANA'] = 'Hrkt'
    scriptMap['OLD_HUNGARIAN'] = 'Hung'
    scriptMap['OLD_ITALIC'] = 'Ital'
    scriptMap['JAVANESE'] = 'Java'
    scriptMap['KAYAH_LI'] = 'Kali'
    scriptMap['KATAKANA'] = 'Kana'
    scriptMap['KHAROSHTHI'] = 'Khar'
    scriptMap['KHMER'] = 'Khmr'
    scriptMap['KHOJKI'] = 'Khoj'
    scriptMap['KANNADA'] = 'Knda'
    scriptMap['KAITHI'] = 'Kthi'
    scriptMap['TAI_THAM'] = 'Lana'
    scriptMap['LAO'] = 'Laoo'
    scriptMap['LATIN'] = 'Latn'
    scriptMap['LEPCHA'] = 'Lepc'
    scriptMap['LIMBU'] = 'Limb'
    scriptMap['LINEAR_A'] = 'Lina'
    scriptMap['LINEAR_B'] = 'Linb'
    scriptMap['LISU'] = 'Lisu'
    scriptMap['LYCIAN'] = 'Lyci'
    scriptMap['LYDIAN'] = 'Lydi'
    scriptMap['MAHAJANI'] = 'Mahj'
    scriptMap['MANDAIC'] = 'Mand'
    scriptMap['MANICHAEAN'] = 'Mani'
    scriptMap['MARCHEN'] = 'Marc'
    scriptMap['MENDE_KIKAKUI'] = 'Mend'
    scriptMap['MEROITIC_CURSIVE'] = 'Merc'
    scriptMap['MEROITIC_HIEROGLYPHS'] = 'Mero'
    scriptMap['MALAYALAM'] = 'Mlym'
    scriptMap['MODI'] = 'Modi'
    scriptMap['MONGOLIAN'] = 'Mong'
    scriptMap['MRO'] = 'Mroo'
    scriptMap['MEETEI_MAYEK'] = 'Mtei'
    scriptMap['MULTANI'] = 'Mult'
    scriptMap['MYANMAR'] = 'Mymr'
    scriptMap['OLD_NORTH_ARABIAN'] = 'Narb'
    scriptMap['NABATAEAN'] = 'Nbat'
    scriptMap['NEWA'] = 'Newa'
    scriptMap['NKO'] = 'Nkoo'
    scriptMap['OGHAM'] = 'Ogam'
    scriptMap['OL_CHIKI'] = 'Olck'
    scriptMap['OLD_TURKIC'] = 'Orkh'
    scriptMap['ORIYA'] = 'Orya'
    scriptMap['OSAGE'] = 'Osge'
    scriptMap['OSMANYA'] = 'Osma'
    scriptMap['PALMYRENE'] = 'Palm'
    scriptMap['PAU_CIN_HAU'] = 'Pauc'
    scriptMap['OLD_PERMIC'] = 'Perm'
    scriptMap['PHAGS_PA'] = 'Phag'
    scriptMap['INSCRIPTIONAL_PAHLAVI'] = 'Phli'
    scriptMap['PSALTER_PAHLAVI'] = 'Phlp'
    scriptMap['PHOENICIAN'] = 'Phnx'
    scriptMap['MIAO'] = 'Plrd'
    scriptMap['INSCRIPTIONAL_PARTHIAN'] = 'Prti'
    scriptMap['REJANG'] = 'Rjng'
    scriptMap['RUNIC'] = 'Runr'
    scriptMap['SAMARITAN'] = 'Samr'
    scriptMap['OLD_SOUTH_ARABIAN'] = 'Sarb'
    scriptMap['SAURASHTRA'] = 'Saur'
    scriptMap['SIGNWRITING'] = 'Sgnw'
    scriptMap['SHAVIAN'] = 'Shaw'
    scriptMap['SHARADA'] = 'Shrd'
    scriptMap['SIDDHAM'] = 'Sidd'
    scriptMap['KHUDAWADI'] = 'Sind'
    scriptMap['SINHALA'] = 'Sinh'
    scriptMap['SORA_SOMPENG'] = 'Sora'
    scriptMap['SUNDANESE'] = 'Sund'
    scriptMap['SYLOTI_NAGRI'] = 'Sylo'
    scriptMap['SYRIAC'] = 'Syrc'
    scriptMap['TAGBANWA'] = 'Tagb'
    scriptMap['TAKRI'] = 'Takr'
    scriptMap['TAI_LE'] = 'Tale'
    scriptMap['NEW_TAI_LUE'] = 'Talu'
    scriptMap['TAMIL'] = 'Taml'
    scriptMap['TANGUT'] = 'Tang'
    scriptMap['TAI_VIET'] = 'Tavt'
    scriptMap['TELUGU'] = 'Telu'
    scriptMap['TIFINAGH'] = 'Tfng'
    scriptMap['TAGALOG'] = 'Tglg'
    scriptMap['THAANA'] = 'Thaa'
    scriptMap['THAI'] = 'Thai'
    scriptMap['TIBETAN'] = 'Tibt'
    scriptMap['TIRHUTA'] = 'Tirh'
    scriptMap['UGARITIC'] = 'Ugar'
    scriptMap['VAI'] = 'Vaii'
    scriptMap['WARANG_CITI'] = 'Wara'
    scriptMap['OLD_PERSIAN'] = 'Xpeo'
    scriptMap['CUNEIFORM'] = 'Xsux'
    scriptMap['YI'] = 'Yiii'
}