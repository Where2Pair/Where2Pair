import org.where2pair.DistanceCalculator;
import org.where2pair.TimeProvider
import org.where2pair.VenueFinder
import org.where2pair.grails.GormVenueDaoService
import org.where2pair.grails.GormVenueRepository
import org.where2pair.grails.VenueToJsonConverter;


beans = {
	venueDaoService(GormVenueDaoService)

	gormVenueRepository(GormVenueRepository) { 
		gormVenueDaoService = ref("venueDaoService") 
	}
	
	venueConverter(VenueToJsonConverter)

	timeProvider(TimeProvider)

	distanceCalculator(DistanceCalculator)
	
	venueFinder(VenueFinder) {
		venueRepository = ref("gormVenueRepository")
		distanceCalculator = ref("distanceCalculator")
	}
}
