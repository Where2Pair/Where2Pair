package org.where2pair.read.venue

import static org.where2pair.read.venue.DayOfWeek.parseDayOfWeek

import groovy.transform.TupleConstructor
import org.where2pair.common.venue.SimpleTime
import org.where2pair.read.venue.opentimes.OpenTimesCriteriaFactory

@TupleConstructor
class OpenTimesCriteriaParser {

    final OpenTimesCriteriaFactory openTimesCriteriaFactory

    OpenTimesCriteria parse(Map<String, ?> params) {
        SimpleTime openFrom = parseOpenFromTimeFromRequest(params)
        SimpleTime openUntil = params.openUntil ? parseSimpleTime(params, 'openUntil') : openFrom

        try {
            DayOfWeek dayOfWeek = params.openDay ? parseDayOfWeek(params.openDay) : null
            return openTimesCriteriaFactory.createOpenTimesCriteria(openFrom, openUntil, dayOfWeek)
        } catch (IllegalArgumentException e) {
            throw new QueryParseException("'openDay' not recognized. Expected to be a day from Monday-Sunday")
        }
    }

    private static SimpleTime parseOpenFromTimeFromRequest(Map<String, ?> params) {
        params.openFrom ? parseSimpleTime(params, 'openFrom') : null
    }

    private static SimpleTime parseSimpleTime(Map<String, ?> params, String paramName) {
        String requestParamValue = params[paramName]
        if (!requestParamValue.contains('.')) {
            throw new QueryParseException("'$paramName' not supplied in the correct format. " +
                    "Expected to be in the form: $paramName:<hour>.<minute>")
        }

        def (hour, minute) = requestParamValue.split(/\./)

        try {
            return new SimpleTime(hour as Integer, minute as Integer)
        } catch (NumberFormatException e) {
            throw new QueryParseException("'$paramName' not supplied in the correct format. " +
                    "Expected to be in the form: $paramName:<hour>.<minute>")
        }
    }

}

