import net.sf.ehcache.util.SlewClock.TimeProvider

import org.where2pair.grails.GrailsVenueDaoService

// Place your Spring DSL code here
beans = {
	
	venueDaoService(GrailsVenueDaoService)
	
	//timeProvider(TimeProvider)
	
//	venueFinder(VenueFinder) {
//		venueRepository = ref("venueRepository")
//		timeProvider = ref("timeProvider")
//	}
}
