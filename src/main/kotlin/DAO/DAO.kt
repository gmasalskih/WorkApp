package DAO

import Data.Entity.Feedback
import Data.Entity.IUser

interface DAO {
    fun createUser(user: IUser): Feedback
}