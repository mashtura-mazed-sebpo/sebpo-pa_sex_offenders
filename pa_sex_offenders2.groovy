package current_scripts


import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.rdc.importer.scrapian.ScrapianContext
import com.rdc.importer.scrapian.model.StringSource
import com.rdc.importer.scrapian.util.ModuleLoader
import com.rdc.scrape.ScrapeAddress
import com.rdc.scrape.ScrapeDob
import com.rdc.scrape.ScrapeEntity
import com.rdc.scrape.ScrapeEvent
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.support.ui.Select

import java.text.SimpleDateFormat
import java.util.regex.Matcher

context.setup([connectionTimeout: 100000, socketTimeout: 505000, retryCount: 1, multithread: true, userAgent: "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:84.0) Gecko/20100101 Firefox/84.0"])
context.session.encoding = "UTF-8"; //change it according to web page's encoding
context.session.escape = true

UPS_Offenders22 script = new UPS_Offenders22(context)
script.initParsing()
/*************Debug*******************/
//script.parseData(null)
//script.getMissingAddress("https://www.pameganslaw.state.pa.us/OffenderDetails/Addresses/8492",null)
/** **********************************/

class UPS_Offenders22 {

    final entityType
    final def moduleFactory = ModuleLoader.getFactory("d897871d8906be0173bebbbf155199ff441dd8d3")
    final ScrapianContext context
    final addressParser
    String chromeDriverPath
    WebDriver driver
/**SCRIPT's initializations**/
    final String root = "https://www.pameganslaw.state.pa.us"
    final String mainUrl = "https://www.pameganslaw.state.pa.us/Search/CountySearchResults?selectedCounty=ADAMS&chkCountyIncarcerated=true&chkCountyIncarcerated=false"
//"https://www.pameganslaw.state.pa.us/Search/SVP_SVDC_Search"
    def CACHED_DATA = []
    String pattern = "dd_MM_yyyy"
    String dateInString = new SimpleDateFormat(pattern).format(new Date())
    final todayFile = "PA_CACHE_" + dateInString + "_.json"
    File JASON_DATA_FILE = new File(todayFile)
/*    String urlsFileName = "PA_SEX_OFF_$dateInString" + "URLS_.txt"
    File urlsFile = new File(urlsFileName)*/
    //loadFrom last run
    File HISTORY_LIST = new File("PA_CACHE_BASE_.json")
    /** *************************************/

    UPS_Offenders22(context) {
        this.context = context
        addressParser = moduleFactory.getGenericAddressParser(context)
        driverSetup()
        JASON_DATA_FILE.write("")
        //urlsFile.write("")
    }

    List detailsLinks = []
    private enum FIELDS
    {
        NAME, ALIASES, EYE_COLOR, GENDER, HAIR_COLOR, HEIGHT, RACE, SCARS, URL, WEIGHT, DOB, EVENTURL, EVENTS, ADDRESS
    }
    def attrMap = [:]
    def entID = 1

    def initParsing() {
        //    invokeBySelenium(mainUrl)
        //println(detailsLinks.size())
        parseData(null)
        driver.quit()
    }

    def parseData(def html) {
        String urls = new File("us_pa_updated_urls2.txt").text
        //String urls = urlsFile.text
        detailsLinks = urls.split(/\n/)
        println("TOTAL: "+detailsLinks.size())
         readFromCache(detailsLinks)
        println("REMAINING : " + detailsLinks.size() + "\n$detailsLinks")
        for (int i = 0; i < detailsLinks.size(); i++) {
            handleDetailsPage(detailsLinks[i])
        }
//        return
//        def details_matcher
//        details_matcher = html =~ /(?i)href="(.+?OffenderDetails.+?)"/
//        while (details_matcher.find()) {
//            handleDetailsPage(details_matcher.group(1))
//        }

    }

    def handleDetailsPage(srcUrl) {
        attrMap = [:]
        def name
        def dob
        def gender
        def race
        def height
        def weight
        def eye_color
        def hair_color
        def scarsList = []
        def aliasList = []
        def addressList = []
        def eventUrl
        if (srcUrl == "") {
            return
        }
        def entityUrl = srcUrl
        attrMap[FIELDS.URL] = entityUrl
        def html
        println("[Invoking ENTITY URL SELENIUM: $entityUrl]")
        println("Parsing $entID $entityUrl")
        html = getHTML(entityUrl)
        if (html =~ /Offender Information Not Available/) {
            return
        }
        entID++
        def nameMatch = html =~ /(?is)<h3>(.*?)<\/h3>/

        if (nameMatch) {
            name = sanitize(nameMatch[0][1].toString().trim())

        }
        def dobMatch = html =~ /(?is)Birth\s*Year.*?<dd>(.*?)<\/dd>/

        if (dobMatch) {
            //dob = "1/1/" + dobMatch[0][1].toString().trim()
            dob = "-/-/" + dobMatch[0][1].toString().trim()
            if (dob)
                attrMap[FIELDS.DOB] = [dob]
        }

        def genderMatch = html =~ /(?is)Gender.*?<dd>(.*?)<\/dd>/

        if (genderMatch) {
            gender = sanitize(genderMatch[0][1].toString().trim())

            if (gender)
                attrMap[FIELDS.GENDER] = [gender]
        }

        def raceMatch = html =~ /(?is)Race.*?<dd>(.*?)<\/dd>/

        if (raceMatch) {
            race = sanitize(raceMatch[0][1].toString().trim())

            if (race)
                attrMap[FIELDS.RACE] = [race]
        }

        def heightMatch = html =~ /(?is)class="row\s*offenderDataRow".*?Height.*?<dd>(.*?)<\/dd>.*?<\/dl>/

        if (heightMatch) {
            height = sanitize(heightFix(heightMatch[0][1].toString().trim()))

            if (height)
                attrMap[FIELDS.HEIGHT] = [height]
        }

        def weightMatch = html =~ /(?is)class="row\s*offenderDataRow".*?Weight.*?<dd>(.*?)<\/dd>.*?<\/dl>/

        if (weightMatch) {
            weight = sanitize(weightMatch[0][1].toString().trim())

            if (weight)
                attrMap[FIELDS.WEIGHT] = [weight]
        }

        def eyeColorMatch = html =~ /(?is)Eye\s*Color.*?<dd>(.*?)<\/dd>/

        if (eyeColorMatch) {
            eye_color = sanitize(eyeColorMatch[0][1].toString().trim())

            if (eye_color)
                attrMap[FIELDS.EYE_COLOR] = [eye_color]
        }

        def hairColorMatch = html =~ /(?is)Hair\s*Color.*?<dd>(.*?)<\/dd>/

        if (hairColorMatch) {
            hair_color = sanitize(hairColorMatch[0][1].toString().trim())

            if (hair_color)
                attrMap[FIELDS.HAIR_COLOR] = [hair_color]
        }

        def scarMatch = html =~ /(?is)Scars,\s*Marks\s*&amp;\s*Tattoos\s*<hr>(.*?)<hr>/



        if (scarMatch) {
            def scarData = scarMatch[0][1].toString().trim()
            if (scarData =~ /<p\s*class="noData">No Scars, Marks or Tattoos reported\.<\/p>/) {

            } else {
                def scarsMatch = scarData =~ /(?is)Type.*?class="gridDataItem br-responsive-sm">(.*?)<\/span>.*?Location.*?class="gridDataItem br-responsive-sm">(.*?)<\/span>.*?Description.*?class="gridDataItem br-responsive-sm">(.*?)<\/span>/

                while (scarsMatch.find()) {
                    /*scarsList.add("Type :" + scarsMatch.group(1).toString().trim() + ", " +
                        "Location :" + scarsMatch.group(2).toString().trim() + ", "
                        + "Description :" + scarsMatch.group(3).toString().trim())*/
                    /*def tempscarData = scarsMatch.group(1).toString().trim() + " - " +
                        scarsMatch.group(2).toString().trim() + " - "
                    + scarsMatch.group(3).toString().trim()*/
                    def tempscarData = fixScarData(scarsMatch)

                    scarsList.add(tempscarData)
                }
            }
        }

        attrMap[FIELDS.SCARS] = scarsList

        def aliasMatch = html =~ /(?is)href="([^"]*)">Aliases<\/a>/

        if (aliasMatch) {
            def aliasUrl = root + aliasMatch[0][1].toString().trim()
            aliasList = alias_capture(aliasUrl, name)
        }

        def addressMatch = html =~ /(?is)href="([^"]*)">Addresses<\/a>/

        if (addressMatch) {
            def addressUrl = root + addressMatch[0][1].toString().trim()
            addressList = address_capture(addressUrl)
        }

        def eventMatch = html =~ /(?is)href="([^"]*)">Offenses<\/a>/

        if (eventMatch) {
            eventUrl = root + eventMatch[0][1].toString().trim()
            attrMap[FIELDS.EVENTURL] = eventUrl
        }
        createEntity(name, attrMap, aliasList, addressList, eventUrl)
        // def attributes = attrMap.values()
        //println("Name: $name\n$attributes\nAliases:$aliasList\nAddress: $addressList\n******")
    }

    def alias_capture(srcUrl, name) {
        def alias_list = []
        println("ALIAS: $srcUrl")
        // def html = invoke(srcUrl)
        def html = getHTML(srcUrl)

        //  def aliasBlockMatch = html =~ /(?is)<div\s*class="row col-sm-12\s*headingWithHR">\s*Aliases(.*?)<\/ul>/
        def aliasBlockMatch = html =~ /(?is)headingWithHR">\s*Aliases(.*?)<\/ul>/
        if (aliasBlockMatch) {
            def aliasData = aliasBlockMatch[0][1].toString().trim()
            def aliasMatch = aliasData =~ /aliasList">(.*?)<\/li>/
            while (aliasMatch.find()) {
                def alias = aliasFix(aliasMatch.group(1).toString().trim(), name)
                if (alias)
                    alias_list.add(alias.replaceAll(/(?s)\s+/, " ").trim())
            }
        }
        return alias_list
    }

    def address_capture(srcUrl) {
        def addrList = []
        println("Address $srcUrl")
        //def html = invoke(srcUrl)
        def html = getHTML(srcUrl)
        def addressBlockMatch = html =~ /(?is)Address<\/span>.*?class="gridDataItem br-responsive-sm">(.*?)<\/div>/
        while (addressBlockMatch.find()) {
            def address = fixAddress(addressBlockMatch.group(1).toString().trim())
            addrList.add(sanitize(address + ",US"))
            // all the addresses are from United States PENNSYLVANIA state
        }
        println(addrList)
        return addrList
    }

    def event_capture_set(srcUrl, ScrapeEntity entity) {
        println("EVENTS: $srcUrl")
        //def html = invoke(srcUrl)
        def html = getHTML(srcUrl)
        def descList = []
        def dateList = []

        def eventMatch = html =~ /(?is)Offense<\/span>.*?class="gridDataItem br-responsive-sm">(.*?)<\/div>/
        while (eventMatch.find()) {
            descList.add(fixEvent(eventMatch.group(1).toString().trim()))
        }
        //   def eventDateMatch = html =~ /(?is)Conviction\s*Date<\/span>.*?class="gridDataItem br-responsive-sm">(.*?)<\/span><\/p>/

        def eventDateMatch = html =~ /(?is)Conviction\s*Date.+?(\d+\/\d+\/\d+)/
        while (eventDateMatch.find()) {
            if (eventDateMatch.group(1).toString().trim() =~ /^<a/) {
                dateList.add("no")
            } else {
                dateList.add(fixEventDate(eventDateMatch.group(1).toString().trim()))
            }
        }
        def eventList = []

        for (int i = 0; i < descList.size(); i++) {
            ScrapeEvent event = new ScrapeEvent()
            def desc = descList.get(i).toString().replaceAll(/(\d+).*?&-&/, '$1 -').replaceAll(/(?s)\s+/, " ").trim()
            //println("DESC: $desc")
            event.setDescription(desc.replaceAll(/(?s)\s+/, " ").trim())
            if (i < dateList.size()) {
                if (!(dateList.get(i) =~ /(?i)no/)) {
                    if (dateList.get(i))
                        event.setDate(dateList.get(i))
                }
            }
            entity.addEvent(event)
            eventList.add([event.description, event.date])
        }
        attrMap[FIELDS.EVENTS] = eventList
        createCache(attrMap, entity)
    }
//============Fixing Data==================//

    def fixAddress(str) {
        str = str.replaceAll(/<\/span>/, ",")
        str = str.replaceAll(/<span.*?>/, "")
        str = str.replaceAll(/<\/p>/, "")
        str = str.replaceAll(/PA&nbsp;/, "PA ")
        str = str.replaceAll(/&nbsp;/, "")
        str = str.replaceAll(/\s+/, " ")
        str = str.replaceAll(/,$/, "")
        return str
    }

    def heightFix(str) {
        str = str.replaceAll(/&#39;/, "'")
        str = str.replaceAll(/&quot;/, "\"")
        return str
    }


    def fixEvent(str) {
        str = str.replaceAll(/<a.*?>/, "")
        str = str.replaceAll(/<\/a>/, "")
        str = str.replaceAll(/\u2011/, "-")
        str = str.replaceAll(/<\/span>/, "")
        str = str.replaceAll(/<\/p>/, "")
        str = str.replaceAll(/&amp;/, "&")
        str = str.replaceAll(/&\w+;/, "&")
        return str
    }

    def fixScarData(Matcher matchResult) {
        def str = ""
        if (matchResult.group(1).toString() =~ /\w+/) {
            if (!(matchResult.group(1).toString() =~ /(?i)Unknown/))
                str = matchResult.group(1).toString().trim() + " - "
        }

        if (matchResult.group(2).toString() =~ /\w+/) {
            if (!(matchResult.group(2).toString() =~ /(?i)Unknown/))
                str = str + matchResult.group(2).toString().trim() + " - "
        }

        if (matchResult.group(3).toString() =~ /\w+/) {
            if (!(matchResult.group(3).toString() =~ /(?i)Unknown/))
                str = str + matchResult.group(3).toString().trim() + " - "
        }
        str = str.replaceAll(/\s*(?:\-|- &)\s*$/, "")

        return str
    }

    def fixEventDate(str) {
        str = str.replaceAll(/\b(\d{1})\b/, "0" + "\$1")
        str = str.replaceAll(/(?i)Yes/, "")

        return str
    }

    def aliasFix(alias, name) {
        //Replacing the First X in the alias with firstname
        if (alias =~ /(?i)^\s*\bX\b/) {
            //Checking if the alias contains two X's in the front
            if (alias =~ /(?i)^(\s*\bX\b\s*)\1/) {
                if (name =~ /(?i)(\b\w+\b\s*){3}/) {
                    def nameMatch = name =~ /^\s*(\s*\b\w+\b){2}/
                    if (nameMatch) {
                        alias = alias.toString().replaceAll(/(?i)^(\s*\bX\b\s*)\1/, nameMatch[0][0].toString() + " ")
                    }
                }
            } else {
                def nameMatch = name =~ /(^\s*\b\w+\b)/
                if (nameMatch) {
                    alias = alias.toString().replaceAll(/(?i)^\bX\b/, nameMatch[0][1].toString())
                }
            }
        }

        //Replacing last X with lastname
        if (alias =~ /(?i)\bX\b\s*$/) {
            if (name =~ /(?i)(\b[\w\-]+\b\s*)JR\.?$/) {
                def nameMatch = name =~ /(?i)(\b[\w\-]+\b\s*)JR\.?$/
                if (nameMatch) {
                    alias = alias.toString().replaceAll(/(?i)\bX\b\s*$/, nameMatch[0][1].toString().trim())
                }
            } else {
                def nameMatch = name =~ /(\b[\w\-]+\b\s*)$/
                if (nameMatch) {
                    alias = alias.toString().replaceAll(/(?i)\bX\b\s*$/, nameMatch[0][1].toString().trim())
                }
            }
        }

        //Replacing last and first X's with lastname and firstname
        if (alias =~ /(?i)^\bX\b.*?\bX\b$/) {
            def nameMatch = name =~ /(?i)^(\b\w+\b).*?(\b[\w\-]+\b\s*)(?:JR)?\.?$/

            if (nameMatch) {
                alias = alias.toString().replaceAll(/(?i)^\bX\b(.*?)\bX\b$/, nameMatch[0][1].toString().trim()
                    + "\$1" + nameMatch[0][2].toString().trim())
            }
        }

        alias = alias.replaceAll(/(?i)\s*\bNone\b\s*/, "")

        return alias
    }

    def sanitize(data) {
        data = data.toString()
        return data.replaceAll(/&amp;|&amp;nbsp;/, '&')
            .replaceAll(/\r\n/, "\n")
            .replaceAll(/(?i)Unknown|nbsp;|amp;/, "")
            .replaceAll(/&\w+;/, "&")
            .replaceAll(/\s*\$\s*$|nbsp;|amp;/, "")
            .replaceAll(/\s*(\,|\$|!|\*|\?|\-|#|:|,|%)\s*$/, "")
            .replaceAll(/(?is)You Are Usin.*/, "").trim()
            .replaceAll(/\s*[,\s,;]+$/, "")
            .replaceAll(/(?s)\s+/, " ")
            .replaceAll(/amp;/, "")
            .replaceAll(/\,\s*$/, "")
            .replaceAll(/(?sm)\s*\r*\n\s*/, " ")
            .replaceAll(/\)$/, "_BR_").trim()
            .replaceAll(/\W+$|^\W+/, "")
            .replaceAll(/_BR_/, ")")
            .replaceAll(/(?s)\s+/, " ")
            .trim()
    }

    def camelCaseConverter(name) {
        //only for person type //\w{2,}: II,III,IV etc ignored
        name = name.replaceAll(/\b(?:((?i:x{0,3}(?:i+|i[vx]|[vx]i{0,3})))|(\w)(\w+))\b/, { a, b, c, d ->
            return b ? b.toUpperCase() : c.toUpperCase() + d.toLowerCase()
        })

        return name
    }

    //============CreateEntity=================//
    def createEntity(name, attrMap, aliasList = [], addrList = [], eventUrl) {
        if (name) {
            def entity
            entity = createPersonEntity(name, aliasList)
            attrMap[FIELDS.NAME] = entity.name
            attrMap[FIELDS.ALIASES] = aliasList
            createEntityCommonCore(entity, attrMap, addrList, eventUrl)
            //println("Entity Created")
        }
    }

    def createPersonEntity(name, aliasList = []) {
        name = personNameReformat(sanitize(name))
        name = camelCaseConverter(name)
        def entity = context.findEntity([name: name])
        if (!entity) {
            entity = context.getSession().newEntity()
            entity.name = name
            entity.type = "P"
        }
        aliasList.each { alias ->
            entity.addAlias(personNameReformat(alias))
        }

        return entity
    }

    def personNameReformat(name) {

        //Regroup person name by comma
        /**
         abc, sdf, jr --match
         abc, sdf jr -- not match
         C. Conway Felton, III -- not match
         O'Dowd, Jr., Charles T. -- match
         * */
        def exToken = "(?:[js]r|I{2,3})"
        return name.replaceAll(/(?i)\s*(.*?)\s*,\s*\b((?:(?!$exToken\b)[^,])+)s*(?:,\s*\b($exToken)\b)?\s*$/, '$2 $1 $3').trim()
    }

    def createEntityCommonCore(ScrapeEntity entity, attrMap, addrList = [], eventUrl) {
        addrList.each { addrStr ->
            def addrMap = addressParser.parseAddress([text: addrStr, /*force_country: true*/])
            def street_sanitizer = { street ->
                return street.replaceAll(/(?s)\s+/, " ")
                    .replaceAll(/^[\s,-]+|\W+$/, "")
            }
            def address1 = addressParser.buildAddress(addrMap, [street_sanitizer: street_sanitizer])
            if (address1) {
                entity.addAddress(address1)

            } else {
                //Address is not parse-able; either add then as raw addr or reformat the input addr string
                //fl.log("Address not parsed for : " + attrMap[FIELDS.URL] + "\n" + addrStr + "\n-----------")
                context.info("Address not parsed for : " + attrMap[FIELDS.URL] + "\n" + addrStr + "\n-----------")
            }
        }
        attrMap[FIELDS.ADDRESS] = entity.addresses

        entity.addUrl(attrMap[FIELDS.URL])

        attrMap[FIELDS.DOB].each {
            entity.addDateOfBirth(new ScrapeDob(it))
        }

        attrMap[FIELDS.GENDER].each {
            entity.addSex(it)
        }

        attrMap[FIELDS.HEIGHT].each {
            it=it+'"'
            entity.addHeight(it)
        }

        attrMap[FIELDS.WEIGHT].each {
            entity.addWeight(it)
        }

        attrMap[FIELDS.EYE_COLOR].each {
            entity.addEyeColor(it)
        }

        attrMap[FIELDS.HAIR_COLOR].each {
            entity.addHairColor(it)
        }

        attrMap[FIELDS.RACE].each {
            entity.addRace(it)
        }

        attrMap[FIELDS.SCARS].each {
            if (it.toString().contains("Tattoo - Arm, right (non-specific) - FLAMES,"))
                println(4)
            def a = sanitize(it).toString()
                .replaceAll(/, \\u0024\\u0024/, "")
                .replaceAll(/,$/, "").trim()
            entity.addScarsMarks(sanitize(a).toString().replaceAll(/\W+$/, ""))
        }

        event_capture_set(eventUrl, entity)
    }
    //=================CACHE MANAGEMENT==================
    def createCache(attrMap, ScrapeEntity entity) {
        CACHED_DATA.add(attrMap)
        String json = new Gson().toJson(CACHED_DATA)
        PrintStream out = new PrintStream(new FileOutputStream(JASON_DATA_FILE))
        out.print(json)
        println("CACHE CREATED\n")
    }

    def readFromCache(def urlsList) {
        def json = new JsonParser().parse(new FileReader(HISTORY_LIST)).getAsJsonArray()
        //  println(json.size())
        for (int i = 0; i < json.size(); i++) {
            attrMap[FIELDS.URL] = json[i].asJsonObject.URL.toString().replaceAll(/"/, "").trim()
            detailsLinks.remove(attrMap[FIELDS.URL])

/*            attrMap[FIELDS.NAME] = json[i].asJsonObject.NAME.toString().replaceAll(/"/, "").trim()
            attrMap[FIELDS.URL] = json[i].asJsonObject.URL.toString().replaceAll(/"/, "").trim()
            attrMap[FIELDS.DOB] = convertToList(json[i], "DOB")
            attrMap[FIELDS.GENDER] = convertToList(json[i], "GENDER")
            attrMap[FIELDS.HAIR_COLOR] = convertToList(json[i], "HAIR_COLOR")
            attrMap[FIELDS.EYE_COLOR] = convertToList(json[i], "EYE_COLOR")
            attrMap[FIELDS.WEIGHT] = convertToList(json[i], "WEIGHT")
            attrMap[FIELDS.HEIGHT] = convertToList(json[i], "HEIGHT")
            attrMap[FIELDS.RACE] = convertToList(json[i], "RACE")
            attrMap[FIELDS.SCARS] = convertToList(json[i], "SCARS")
            attrMap[FIELDS.EVENTURL] = json[i].asJsonObject.EVENTURL.toString().replaceAll(/"/, "").trim()
            attrMap[FIELDS.ALIASES] = convertToList(json[i], "ALIASES")
            attrMap[FIELDS.EVENTS] = json[i].asJsonObject.EVENTS
            attrMap[FIELDS.ADDRESS] = json[i].asJsonObject.ADDRESS
            create_CACHE_ENTITY(attrMap, json[i])
            detailsLinks.remove(attrMap[FIELDS.URL])
            attrMap.clear()*/
        }
    }

    def convertToList(JsonObject jsonObject, String key) {

        JsonArray array = jsonObject.getAsJsonArray(key)
        if (array == null)
            return
        def outerList = []
        for (int i = 0; i < array.size(); i++) {
            outerList.add(array.get(i).getAsString())
        }
        return outerList
    }

    def create_CACHE_ENTITY(LinkedHashMap attrMap, def json) {
        def entity = context.findEntity([name: attrMap[FIELDS.NAME]])
        if (!entity) {
            entity = context.getSession().newEntity()
            entity.name = attrMap[FIELDS.NAME]
            entity.type = "P"
        }
        attrMap[FIELDS.ALIASES].each { it ->
            entity.addAlias(it.toString().replaceAll(/"/, "").trim())
        }
        entity.addUrl(attrMap[FIELDS.URL])

        attrMap[FIELDS.DOB].each {
            entity.addDateOfBirth(new ScrapeDob(it.toString().replaceAll(/"/, "").trim()))
        }

        attrMap[FIELDS.GENDER].each {
            entity.addSex(it.toString().replaceAll(/"/, "").trim())
        }

        attrMap[FIELDS.HEIGHT].each {
            entity.addHeight(it.toString().replaceAll(/"/, "").trim())
        }

        attrMap[FIELDS.WEIGHT].each {
            entity.addWeight(it.toString().replaceAll(/"/, "").trim())
        }

        attrMap[FIELDS.EYE_COLOR].each {
            entity.addEyeColor(it.toString().replaceAll(/"/, "").trim())
        }

        attrMap[FIELDS.HAIR_COLOR].each {
            entity.addHairColor(it.toString().replaceAll(/"/, "").trim())
        }

        attrMap[FIELDS.RACE].each {
            entity.addRace(it.toString().replaceAll(/"/, "").trim())
        }

        attrMap[FIELDS.SCARS].each {
            if (it.toString().contains("Tattoo - Arm, right (non-specific) - FLAMES,"))
                println(4)
            def a = sanitize(it.toString().replaceAll(/"/, "").trim())
                .replaceAll(/, \\u0024\\u0024/, "")
                .replaceAll(/,$/, "").trim()
            a = sanitize(a).toString()
            entity.addScarsMarks(a.toString())

        }
        for (int i = 0; i < attrMap[FIELDS.EVENTS].size(); i++) {
            def eventDetails = attrMap[FIELDS.EVENTS][i]
            ScrapeEvent event = new ScrapeEvent()
            event.description = eventDetails[0].toString().replaceAll(/"/, "")
            def date = eventDetails[1].toString()
            if (date) {
                date = date.toString().replaceAll(/"/, "").trim()
                date = context.parseDate(new StringSource(date), ["MM/dd/yyyy"] as String[])
                event.setDate(date)
            }
            entity.addEvent(event)
        }
        def address1 = attrMap[FIELDS.ADDRESS]
        ScrapeAddress address = new ScrapeAddress()
        def checker = false
        if (address1) {
            address1.each { it ->
                def street = it.address1.toString().replaceAll(/"/, "")
                def city = it.city.toString().replaceAll(/"/, "")
                def province = it.province.toString().replaceAll(/"/, "")
                def postal = it.postalCode.toString().replaceAll(/"/, "")
                def country = it.country.toString().replaceAll(/"/, "")
                if (!(street =~ /(null|^\W+$)/)) {
                    address.address1 = street
                } else {
                    checker = getMissingAddress(attrMap[FIELDS.URL], entity, json)
                    if (checker) {
                        city = province = postal = country = null
                        return
                    }
                }
                if (!(city =~ /(null|^\W+$)/)) {
                    address.city = city
                }
                if (!(province =~ /(null|^\W+$)/)) {
                    address.province = province
                }
                if (!(postal =~ /(null|^\W+$)/)) {
                    address.postalCode = postal
                }
                if (!(country =~ /(null|^\W+$)/)) {
                    address.country = country
                }
                // def fullAddress=it.toString().replaceAll(/"/,"").trim()
                if (!checker) {
                    entity.addAddress(address)
                }
            }
        } else {
            address.province = "PENNSYLVANIA"
            address.country = "UNITED STATES"
            entity.addAddress(address)
        }

        println("Entity: $entity.addresses\n")
    }

    def getMissingAddress(def entityUrl, ScrapeEntity entity, json) {
        println("WORKING ON MISSING ADDRESSES FOR:$entity.name")
        def html = getHTML(entityUrl)
        def addressMatch = html =~ /(?is)href="([^"]*)">Addresses<\/a>/
        def addressList
        if (addressMatch) {
            def addressUrl = root + addressMatch[0][1].toString().trim()
            addressList = address_capture(addressUrl)
        }
        addressList.each { addrStr ->
            def addrMap = addressParser.parseAddress([text: addrStr, /*force_country: true*/])
            def street_sanitizer = { street ->
                return street.replaceAll(/(?s)\s+/, " ")
                    .replaceAll(/^[\s,-]+|\W+$/, "")
            }
            def address1 = addressParser.buildAddress(addrMap, [street_sanitizer: street_sanitizer])
            if (address1) {
                entity.addAddress(address1)
                return true
            } else {
                context.info("ADDRESS COULD NOT BE PARSED")
            }
        }

    }
//====================================================
//========================Invoke================//

    def getHTML(def srcUrl) {
        try {
            println("ACCEPT/DECLINE : $srcUrl")
            driver.get(srcUrl)
            Thread.sleep(15000)
            driver.findElement(new By.ByXPath("//div[@class='AcceptDeclineButton']//form//button")).click()
            Thread.sleep(5000)
            return driver.getPageSource()
        } catch (Exception e) {
            driver.get(srcUrl)
            Thread.sleep(5000)
            //println("Invoked 2: $srcUrl")
            return driver.getPageSource()
        } catch (Exception e) {
            e.printStackTrace()
        }

    }

    def invokeBySelenium(def url) {
// Get root page
        driver.get(url)
        Thread.sleep(10000)
        try {
            driver.findElement(new By.ByXPath("//div[@class='AcceptDeclineButton']//form//button")).click()
            Thread.sleep(10000)
            WebElement dropDown = driver.findElement(By.xpath("//*[@id=\"Countydropdown\"]"))
            Select options = new Select(dropDown)
            List<WebElement> county = options.getOptions()
            List<String> countyNames=new LinkedList<>()
            println(county.size())
            def countyName = ""
            for (int i = 1; i < county.size(); i++) {
                countyNames.add(county[i].text)

            }
            for(String cN: countyNames){
                countyName=cN
                context.info("RECENT COUNTY: <<<<<<< $countyName >>>>>")
                getDataFromCounties(countyName)
                context.info("COMPLETED : $countyName >>>>>>>>>>>>>>>>>>>>>>")
            }
            driver.close()
            driver.quit()
        } catch (Exception e) {
            e.printStackTrace()
        }

    }

    def getDataFromCounties(String countyName) {
        def page = 1
        def url = "https://www.pameganslaw.state.pa.us/Search/CountySearchResults?page=$page&selectedCounty=$countyName&selectedSortBy=1&chkCountyIncarcerated=True"
        driver.get(url)
        context.info("context.info: driver hitting url :" + url)
        Thread.sleep(10000)
        try {
            driver.findElement(By.xpath("//button[@class='btn btn-success btn-sm']")).click()
            Thread.sleep(10000)
        } catch (Exception e) {

        }
        def lastPage
        try {
            lastPage = driver.findElement(new By.ByXPath("/html/body/div[4]/form/div[2]/div/div[1]/div/ul/li[9]/a"))
                .getAttribute("href")
                .toString()
                .replaceAll(/.*?page=(\d+).*/, '$1')
            lastPage = Integer.parseInt(lastPage)
        } catch (Exception e) {
            def total = driver.findElement(new By.ByXPath("/html/body/div[4]/form/div[2]/div/div[1]/div/ul"))
                .getText()
            total = total.replaceAll(/(?ism)^(.+?\s+of\s+)(\d+\s+)(.+)$/, '$2').toInteger()
            lastPage = Math.ceil(total / 50).toInteger()
            println("Total: $total LAST PAGE: $lastPage\n")
        }
        println("Total Page: $lastPage")
        for (; page <= lastPage; page++) {
            url = "https://www.pameganslaw.state.pa.us/Search/CountySearchResults?page=$page&selectedCounty=$countyName&selectedSortBy=1&chkCountyIncarcerated=True"
            context.info("SELENIUM INVOKE: $url")
            driver.get(url)
            Thread.sleep(5000)
            List<WebElement> links = driver.findElements(By.xpath("//a[@class='bodyLink']"))
            for (def element : links) {
                String link = element.getAttribute("href")
                context.info("GOT: $link")
                urlsFile.append(link + "\n")
                // detailsLinks.add(link)
            }
            //parseData(driver.getPageSource())
        }

    }
//System SET UP//
    def driverSetup() {
        //Change the chrome driver path according to the system
        // chromeDriverPath = "/usr/bin/chromedriver"
        chromeDriverPath = "/home/mashtura/RDC_WEBSCRAPING/p200130_rdcscrapper/assets/selenium_driver/chromedriver"

        System.setProperty("webdriver.chrome.driver", chromeDriverPath)
        ChromeOptions options = new ChromeOptions()

        options.addArguments(
            //  "--headless",
            "--disable-gpu",
            "--ignore-certificate-errors",
            "--window-size=1500,1000",
            "--silent",
            "--blink-settings=imagesEnabled=false" // Don't load images
        )
        //
        driver = new ChromeDriver(options)
    }

    def invoke(url, headersMap = [:], cache = true, tidy = false, miscData = [:]) {
        Map attrMap = [url: url, tidy: tidy, headers: headersMap, cache: cache]
        attrMap.putAll(miscData)
        return context.invoke(attrMap)
    }
}



