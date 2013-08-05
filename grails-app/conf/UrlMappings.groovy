class UrlMappings {

	static mappings = {
		"/venues/nearest"(controller: "venueFinder", action: "findNearest")
		"/venues"(controller: "venue", action: "showAll")
		"/venue/$id?"(resource: "venue")

		"/"(view:"/index")
		"500"(view:'/error')
	}
}
