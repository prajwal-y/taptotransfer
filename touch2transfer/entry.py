import cgi
import json
import urllib
import webapp2

from google.appengine.api import users
from google.appengine.api import urlfetch
from google.appengine.ext import db

class User(db.Model):
	uid = db.StringProperty()
	regId = db.StringProperty()
	ip = db.StringProperty()

def user_key(uid = None):
	return db.Key.from_path('RegisterDevice', uid or 'default-user')

class RegisterDevice(webapp2.RequestHandler):
	def post(self):
		data = json.loads(self.request.body)
		user = User(parent=user_key(data['phoneNo']))
		user.uid = data['phoneNo']
		user.ip = data['ipAddr']
		user.regId = data['gcmRegId']
		user.put()
		result = SendMessage(user.regId)
		self.response.out.write('<html><body>')
		self.response.out.write('Response was %s' % result)
		self.response.out.write('</body></html>')
		

def SendMessage(registrationId):
	url = "https://android.googleapis.com/gcm/send"
	form_fields = { 'registration_ids' : [registrationId], 'data' : { 'message' : 'Hello from GCM' } }
        #form_data = urllib.urlencode(form_fields)
	form_data = json.dumps(form_fields)
        #result = form_data
        result = urlfetch.fetch(url=url,payload=form_data,method=urlfetch.POST,headers={'Content-Type': 'application/json','Authorization' : 'key=AIzaSyDhG_uSZapcePFTH87ucv0OFlJQGwr2now'})
        return result.content        

class TestData(webapp2.RequestHandler):
	def get(self):
		#data = json.loads(self.request.body)
		self.response.out.write('<html><body>')
		users = db.GqlQuery("SELECT * "
							"FROM User "
							"LIMIT 10")
		for user in users:
			self.response.out.write(
			'Phone number is <b>%s</b>:' % user.uid)
			self.response.out.write(
			'IP address is <b>%s</b>:' % user.ip)

app = webapp2.WSGIApplication([('/registerDevice', RegisterDevice),
							   ('/testData', TestData)],                            
							  debug=True)
