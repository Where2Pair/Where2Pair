import mash.MongoModule
import mash.MongoService

import static org.ratpackframework.groovy.RatpackScript.ratpack

def indexPages = ["index.html"] as String[]

ratpack {

    modules {
        register new MongoModule(new File('config.groovy'))
    }

    handlers { MongoService mongoService ->

        prefix('api') {

            // validation method to make sure we can connect to our DB
            get('collections') {
                def collections = mongoService.collections()
                response.send "Available database collections are $collections"
            }

            prefix('person') {

                get {
                    response.send 'application/json', mongoService.find('person', '{}', '{"name":1,"image":1}')
                }

                post {
                    accepts.type('application/json') {
                        mongoService.save('person', request.text)
                        response.send()
                    }.send()
                }

                get(':id') {
                    def queryById = """{ "_id": { "\$oid": "${pathTokens.id}" }}"""
                    def person = mongoService.findOne('person', queryById)
                    if (person) {
                        response.send 'application/json', person
                    } else {
                        clientError 404
                    }
                }

            }

            prefix('image') {

                post {
                    def image = decoder.decodeBuffer(request.text as String)
                    def filename = mongoService.createFile('images', image, contentType)
                    response.send 'application/json', "{'filename':'${filename}'}"
                }

                get(':filename') {
                    def image = mongoService.fetchFile('images', pathTokens.filename)
                    if(image){
                        byte[] imageBytes = org.apache.commons.io.IOUtils.toByteArray(image.inputStream)
                        response.send image.contentType, io.netty.buffer.Unpooled.wrappedBuffer(imageBytes)
                    } else {
                        clientError 404
                    }
                }

            }
        }

        assets "public", indexPages
    }
}