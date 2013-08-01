class UrlMappings {

	static mappings = {
		"/venues/nearest"(controller: "venueFinder", action: "findNearest")
		"/venue"(controller: "venue", action: "showAll")
		"/venue/$id"(resource: "venue")

		"/"(view:"/index")
		"500"(view:'/error')
	}
}
