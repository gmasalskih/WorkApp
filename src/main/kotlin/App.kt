import DAO.Web.WebHelper.RestApi
import io.reactivex.Observable
import org.apache.poi.ss.formula.functions.T
import java.util.*

object App {

    private val api = RestApi.webApi

    val map = HashMap<String, String>()

    fun go() {
        map.put("gcid", "1039")
//        map.put("", "")
//        map.put("","")
//        map.put("","")
//        map.put("","")


//        test(api::createUser, "User")
//        test(api::addPersonToGroup, "1039", "55851")
//        test(api::getPersonsFromGroup, "1039")
        test(api::getGroup, map)



    }

    fun <P1, P2, R> test(fanApi: (P1, P2) -> Observable<R>, param1: P1, param2: P2) {
        fanApi(param1, param2).blockingSubscribe {
            println(it)
        }
    }


    fun <T, R> test(fanApi: (T) -> Observable<R>, param: T) {
        fanApi(param).blockingSubscribe {
            println(it)
        }
    }

}