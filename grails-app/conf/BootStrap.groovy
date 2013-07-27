import org.where2pair.grails.GrailsOpenPeriod
import org.where2pair.grails.GrailsVenue

class BootStrap {

    def init = { servletContext ->
		GrailsVenue venue = new GrailsVenue(latitude: 1.0, longitude: 0.1)
		venue.addToOpenPeriods(day: 6, openHour: 12, closeHour: 30)
		venue.save()
	}
    
	def destroy = {
    }
}
