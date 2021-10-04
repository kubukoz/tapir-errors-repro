package demo

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import cats.effect.IO
import cats.effect.IOApp
import com.comcast.ip4s._
import org.http4s.HttpRoutes
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import sttp.tapir.Schema
import sttp.tapir.infallibleEndpoint
import sttp.tapir.json.circe._
import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter
import sttp.tapir.server.akkahttp.AkkaHttpServerOptions
import sttp.tapir.server.http4s.Http4sServerInterpreter
import sttp.tapir.server.http4s.Http4sServerOptions
import sttp.tapir.server.interceptor.ValuedEndpointOutput
import sttp.tapir.server.interceptor.exception.ExceptionContext

import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.io.StdIn

object Main extends IOApp.Simple {

  def run: IO[Unit] =
    EmberServerBuilder
      .default[IO]
      .withHost(host"0.0.0.0")
      .withPort(port"8083")
      .withHttpApp {

        Http4sServerInterpreter[IO](
          Http4sServerOptions
            .customInterceptors[IO, IO]
            .errorOutput(msg => ValuedEndpointOutput(jsonBody[String], "failed"))
            .options
        )
          .toRoutes(
            infallibleEndpoint
              .in("run")
              .serverLogicInfallible(_ => IO.raiseError[Unit](new Throwable("unexpected error!")))
          )
          .orNotFound
      }
      .build
      .useForever

}

object MainAkka extends App {
  implicit val as = ActorSystem()

  val route: Route = AkkaHttpServerInterpreter(
    AkkaHttpServerOptions
      .customInterceptors
      .errorOutput(msg => ValuedEndpointOutput(jsonBody[String], "failed"))
      .options
  )
    .toRoute(
      infallibleEndpoint
        .in("run")
        .serverLogicInfallible(_ => Future.failed[Unit](new Throwable("unexpected error!")))
    )

  Http(as)
    .newServerAt("0.0.0.0", 8083)
    .bind(route)
}
