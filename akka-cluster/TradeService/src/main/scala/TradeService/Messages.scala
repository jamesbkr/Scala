package TradeService
case class Add(num1: Int, num2: Int)
case object BackendRegistration
case class Login(name: String, password: String)
case object AccountServiceRegistration
case class Trade(ticker: String, action: String)
case object TradeServiceRegistration
