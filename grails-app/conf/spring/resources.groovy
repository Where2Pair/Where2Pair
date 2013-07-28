import org.where2pair.TimeProvider
import org.where2pair.VenueFinder
import org.where2pair.grails.GormVenueDaoService
import org.where2pair.grails.GormVenueRepository


beans = {
	
	venueDaoService(GormVenueDaoService)
	
	venueRepository(GormVenueRepository) {
		gormVenueDaoService = ref("venueDaoService")
	}
	
	timeProvider(TimeProvider)
	
	venueFinder(VenueFinder) {
		venueRepository = ref("venueRepository")
		timeProvider = ref("timeProvider")
	}
}
