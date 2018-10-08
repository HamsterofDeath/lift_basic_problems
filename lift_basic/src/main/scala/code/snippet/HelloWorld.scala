package code
package snippet

import scala.xml.{NodeSeq, Text}

import net.liftweb.util._
import net.liftweb.common._
import java.util.Date

import code.lib._
import Helpers._
import net.liftweb.http.SHtml
import net.liftweb.http.js.JsCmds

class HelloWorld {
  lazy val date: Box[Date] = DependencyFactory.inject[Date] // inject the date

  // replace the contents of the element with id "time" with the date
  def howdy = {
    var counter = 0
    "#time *" #> date.map(_.toString) &
    "#workingAjax *" #> {
      def nodeSeq: NodeSeq = {
        SHtml.ajaxButton(s"you pressed this button $counter times", () => {
          JsCmds.SetHtml("workingAjax", nodeSeq)
        })
      }

      nodeSeq

    }
  }

  /*
   lazy val date: Date = DependencyFactory.time.vend // create the date via factory

   def howdy = "#time *" #> date.toString
   */
}

