class UrlMappings {

	static mappings = {
		"/venue/findNearest"(controller: "venueFinder", action: "findNearest")
		"/venue/$id?"(resource: "venue")

		"/"(view:"/index")
		"500"(view:'/error')
	}
}
