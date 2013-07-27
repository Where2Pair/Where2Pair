class UrlMappings {

	static mappings = {
		"/venue/"(resource: "venue")

		"/"(view:"/index")
		"500"(view:'/error')
	}
}
