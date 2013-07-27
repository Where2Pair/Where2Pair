import org.where2pair.TimeProvider
import org.where2pair.VenueFinder
import org.where2pair.grails.GrailsVenueDaoService
import org.where2pair.grails.GrailsVenueRepository


beans = {
	
	venueDaoService(GrailsVenueDaoService)
	
	venueRepository(GrailsVenueRepository) {
		grailsVenueDaoService = ref("venueDaoService")
	}
	
	timeProvider(TimeProvider)
	
	venueFinder(VenueFinder) {
		venueRepository = ref("venueRepository")
		timeProvider = ref("timeProvider")
	}
}
