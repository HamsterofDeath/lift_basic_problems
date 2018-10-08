package code
package snippet

import scala.xml.{NodeSeq, Text}

import net.liftweb.util._
import net.liftweb.common._
import java.util.Date

import code.lib._
import Helpers._
import net.liftweb.http.S.{SFuncHolder, encodeURL, fmapFunc}
import net.liftweb.http.{LiftRules, SHtml}
import net.liftweb.http.js.JsCmds

class HelloWorld {
  lazy val date: Box[Date] = DependencyFactory.inject[Date] // inject the date

  // replace the contents of the element with id "time" with the date
  def howdy = {
    var counter = 0
    "#time *" #> date.map(_.toString) &
    "#workingAjax *" #> {
      def nodeSeq: NodeSeq = {
        SHtml.ajaxButton(s"you pressed the buttons $counter times", () => {
          counter += 1
          // this works as expected
          JsCmds.SetHtml("workingAjax", nodeSeq)
        })
      }

      nodeSeq
    } &
    "#indirectWorkingAjax *" #> {
      def nodeSeq: NodeSeq = {
        SHtml.ajaxButton(s"you pressed the buttons $counter times", () => {
          counter += 1
          val newContent = nodeSeq.toString
                           .replaceAllLiterally("\"", "\\\"")

          // this also works
          val bypass = {
            s"""
               |var step = "$newContent"
               |bypasser(step)
             """.stripMargin
          }
          JsCmds.Run(bypass)
        })
      }

      nodeSeq
    } &
    "#brokenAjax *" #> {
      var myFunctionId = ""
      def nodeSeq(target: String): NodeSeq = {
        // the ajax callback of this button gets garbage collected
        SHtml.ajaxButton(s"you pressed the buttons $counter times", () => {
          println("call reached")
          SHtml.makeAjaxCall(target).cmd
        })
      }

      val f = (s: String) => {
        println(s"received: $s")
        counter += 1
        JsCmds.SetHtml("brokenAjax", nodeSeq(myFunctionId))
      }

      fmapFunc(SFuncHolder(f)) { func =>
        val where: String = encodeURL(LiftRules.liftPath + "/ajax/" + "?" + func + "=foo")
        myFunctionId = func
        println(s"function id is $func")
        nodeSeq(func)
      }
    }
  }
}

