import org.where2pair.DistanceCalculator;
import org.where2pair.TimeProvider
import org.where2pair.VenueFinder
import org.where2pair.grails.GormVenueDaoService
import org.where2pair.grails.GormVenueRepository
import org.where2pair.grails.VenueJsonMarshaller;


beans = {
	venueDaoService(GormVenueDaoService)

	venueRepository(GormVenueRepository) { 
		gormVenueDaoService = ref("venueDaoService") 
	}
	
	venueJsonMarshaller(VenueJsonMarshaller)

	timeProvider(TimeProvider)

	distanceCalculator(DistanceCalculator)
	
	venueFinder(VenueFinder) {
		venueRepository = ref("venueRepository")
		distanceCalculator = ref("distanceCalculator")
	}
}
