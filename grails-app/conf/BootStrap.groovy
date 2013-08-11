import static org.where2pair.DayOfWeek.MONDAY
import static org.where2pair.DayOfWeek.SUNDAY
import org.where2pair.grails.GormVenue

class BootStrap {

    def init = { servletContext ->
		environments {
			development {
				(MONDAY..SUNDAY).each { day ->
					(1d..10d).each {
						GormVenue venue = new GormVenue(name: "place-" + it, 
							latitude: 1.0 + it/10, 
							longitude: 0.1 + it/10,
							addressLine1: '123 Some Street',
							city: 'London',
							postcode: 'postcode')
						venue.addToOpenPeriods(day: day, openHour: 8, closeHour: 30)
						venue.save()
					}
				}
			}
		}
	}
    
	def destroy = {
    }
}
