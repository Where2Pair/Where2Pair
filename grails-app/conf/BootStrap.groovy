import org.where2pair.DayOfWeek
import org.where2pair.grails.GormVenue

class BootStrap {

    def init = { servletContext ->
		environments {
			development {
				GormVenue venue = new GormVenue(latitude: 1.0, longitude: 0.1)
				venue.addToOpenPeriods(day: DayOfWeek.SUNDAY, openHour: 12, closeHour: 30)
				venue.save()
			}
		}
	}
    
	def destroy = {
    }
}
