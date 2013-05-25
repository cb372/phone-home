package com.github.cb372.phonehome

import org.scalatra._

class StaticResourcesController extends ScalatraServlet {

  notFound {
    serveStaticResource() getOrElse resourceNotFound()
  }

}
