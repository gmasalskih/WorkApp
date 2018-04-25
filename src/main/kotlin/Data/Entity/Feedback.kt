package Data.Entity

import Util.Const.Result
import java.io.Serializable

interface Feedback {
    val result: Result
    var response: Serializable?
}