package org.where2pair.venue

import org.where2pair.venue.DailyOpeningTimes.SimpleTime
import spock.lang.Specification

import static org.where2pair.venue.DayOfWeek.*

class OpenHoursJsonMarshallerSpec extends Specification {

    WeeklyOpeningTimes weeklyOpeningTimes
    Map openHoursJson
    OpenHoursJsonMarshaller openHoursJsonMarshaller = new OpenHoursJsonMarshaller()

    def "converts weekly opening times to json"() {
        when:
        Map result = openHoursJsonMarshaller.asOpenHoursJson(weeklyOpeningTimes)

        then:
        result == openHoursJson
    }

    def "converts json to weekly opening times"() {
        when:
        WeeklyOpeningTimes result = openHoursJsonMarshaller.asWeeklyOpeningTimes(openHoursJson)

        then:
        result == weeklyOpeningTimes
    }

    def "converts json to weekly opening times when strings are supplied for numerical values"() {
        given:
        openHoursJson['monday'] = [[openHour: '12', openMinute: '0', closeHour: '18', closeMinute: '30']]

        when:
        WeeklyOpeningTimes result = openHoursJsonMarshaller.asWeeklyOpeningTimes(openHoursJson)

        then:
        result == weeklyOpeningTimes
    }

    void setup() {
        weeklyOpeningTimes = new WeeklyOpeningTimesBuilder()
                .addOpenPeriod(MONDAY, new SimpleTime(12, 0), new SimpleTime(18, 30))
                .addOpenPeriod(TUESDAY, new SimpleTime(8, 0), new SimpleTime(11, 0))
                .addOpenPeriod(FRIDAY, new SimpleTime(9, 0), new SimpleTime(12, 0))
                .addOpenPeriod(FRIDAY, new SimpleTime(13, 0), new SimpleTime(15, 0)).build()

        openHoursJson = [monday: [[openHour: 12, openMinute: 0, closeHour: 18, closeMinute: 30]],
                tuesday: [[openHour: 8, openMinute: 0, closeHour: 11, closeMinute: 0]],
                wednesday: [],
                thursday: [],
                friday: [[openHour: 9, openMinute: 0, closeHour: 12, closeMinute: 0],
                        [openHour: 13, openMinute: 0, closeHour: 15, closeMinute: 0]],
                saturday: [],
                sunday: []] as LinkedHashMap
    }
}
