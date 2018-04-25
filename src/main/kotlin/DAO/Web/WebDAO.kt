package DAO.Web

import DAO.DAO
import Data.Entity.Feedback
import Data.Entity.IUser
import Util.Const.Result
import java.io.Serializable

object WebDAO : DAO {
    override fun createUser(user: IUser): Feedback {
        return object : Feedback{
            override val result = Result.OK
            override var response: Serializable? = ""
        }
    }
}