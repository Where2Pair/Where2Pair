import org.where2pair.grails.GormRole
import org.where2pair.grails.GormUser
import org.where2pair.grails.GormUserGormRole

import static org.where2pair.DayOfWeek.MONDAY
import static org.where2pair.DayOfWeek.SUNDAY
import org.where2pair.grails.GormVenue

class BootStrap {

    def init = { servletContext ->
		environments {
			development {
				
				Set availableFeatures = ['wifi', 'mobile payments', 'baby changing']
				Random random = new Random()
				(MONDAY..SUNDAY).each { day ->
					(1d..10d).each {
						Set features = availableFeatures.findResults { if (random.nextBoolean()) return it }
						GormVenue venue = new GormVenue(name: "place-" + it, 
							latitude: 1.0 + it/10, 
							longitude: 0.1 + it/10,
							addressLine1: '123 Some Street',
							city: 'London',
							postcode: 'postcode',
							features: features)
						venue.addToOpenPeriods(day: day, openHour: 8, closeHour: 30)
						venue.save()
					}
				}
			}
		}

        def adminRole = new GormRole(authority: 'ROLE_ADMIN').save(flush: true)
        def userRole = new GormRole(authority: 'ROLE_USER').save(flush: true)

        def testUser = new GormUser(username: 'testUser', enabled: true, password: 'password')
        testUser.save(flush: true)

        GormUserGormRole.create testUser, adminRole, true
	}
    
	def destroy = {
    }
}
