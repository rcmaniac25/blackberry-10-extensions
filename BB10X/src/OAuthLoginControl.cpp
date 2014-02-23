/*
 * BlackBerry 10 Extension library
 * Copyright (c) 2013-2014 Vincent Simonetti
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
 * to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

#include "OAuthLoginControl.h"

#include <bb/cascades/Container>
#include <bb/cascades/ScrollView>
#include <bb/cascades/WebView>
#include <bb/cascades/WebNavigationRequest>
#include <bb/cascades/Color>

#include <bb/data/JsonDataAccess>

#include <QNetworkAccessManager>
#include <QNetworkRequest>

using namespace bb::cascades;
using namespace rebuild::ui::component;

//XXX Should probably wrap https://github.com/pipacs/o2

OAuthLoginControl::OAuthLoginControl(Container* parent) : CustomControl(parent),
	_token(), _refreshToken(), _tokenExpire(), _autoRefresh(false),
	_defUrl(), _authUrl(), _authRefreshUrl(), _redirectUrl(),
	running(false)
{
	_netAccess = new QNetworkAccessManager(this);
	bool res = QObject::connect(_netAccess, SIGNAL(finished(QNetworkReply*)), this, SLOT(onLoginRequestFinished(QNetworkReply*)));
	Q_ASSERT(res);
	Q_UNUSED(res)

	_browser = WebView::create().
			connect(SIGNAL(navigationRequested(bb::cascades::WebNavigationRequest*)), this, SLOT(verifyLogin(bb::cascades::WebNavigationRequest*))).
			connect(SIGNAL(minContentScaleChanged(float)), this, SLOT(browserMinContentScaleChanged(float))).
			connect(SIGNAL(maxContentScaleChanged(float)), this, SLOT(browserMaxContentScaleChanged(float)));
	_sv = ScrollView::create(Container::create().background(Color::LightGray).add(_browser)).
			scrollMode(ScrollMode::Vertical).pinchToZoomEnabled(false).overScrollEffectMode(OverScrollEffectMode::OnScroll);
	setRoot(_sv);
}

OAuthLoginControl::~OAuthLoginControl()
{
}

QUrl OAuthLoginControl::defaultUrl() const
{
	return _defUrl;
}

void OAuthLoginControl::setDefaultUrl(QUrl url)
{
	if(_defUrl != url)
	{
		bool changeBrowser = _browser->url() == _defUrl;

		_defUrl = url;
		emit defaultUrlChanged(url);

		if(changeBrowser)
		{
			_browser->setUrl(url);
		}
	}
}

QUrl OAuthLoginControl::authUrl() const
{
	return _authUrl;
}

void OAuthLoginControl::setAuthUrl(QUrl url)
{
	if(_authUrl != url)
	{
		_authUrl = url;
		emit authUrlChanged(url);

		setTokenInfo(QString::null, QString::null, QDateTime());
	}
}

QUrl OAuthLoginControl::tokenUrl() const
{
	return _authRefreshUrl;
}

void OAuthLoginControl::setTokenUrl(QUrl url)
{
	if(_authRefreshUrl != url)
	{
		_authRefreshUrl = url;
		emit tokenUrlChanged(url);

		setTokenInfo(QString::null, QString::null, QDateTime());
	}
}

QUrl OAuthLoginControl::redirectUrl() const
{
	return _redirectUrl;
}

void OAuthLoginControl::setRedirectUrl(QUrl url)
{
	if(_redirectUrl != url)
	{
		_redirectUrl = url;
		emit redirectUrlChanged(url);
	}
}

QString OAuthLoginControl::clientId() const
{
	return _clientId;
}

void OAuthLoginControl::setClientId(QString clientId)
{
	if(_clientId != clientId)
	{
		_clientId = clientId;
		emit clientIdChanged(clientId);

		setTokenInfo(QString::null, QString::null, QDateTime());
	}
}

QString OAuthLoginControl::clientSecret() const
{
	return _clientSecret;
}

void OAuthLoginControl::setClientSecret(QString clientSecret)
{
	if(_clientSecret != clientSecret)
	{
		_clientSecret = clientSecret;
		emit clientSecretChanged(clientSecret);

		setTokenInfo(QString::null, QString::null, QDateTime());
	}
}

QString OAuthLoginControl::scope() const
{
	return _scope;
}

void OAuthLoginControl::setScope(QString scope)
{
	if(_scope != scope)
	{
		_scope = scope;
		emit scopeChanged(scope);
	}
}

#define IS_LOGGED_IN (!(_token.isNull() || _token.isEmpty()) && !(_refreshToken.isNull() || _refreshToken.isEmpty()) && _tokenExpire.isValid())

bool OAuthLoginControl::validateAndCheckLoggedIn()
{
	if(_tokenExpire.isValid() && _tokenExpire <= QDateTime::currentDateTimeUtc())
	{
		if(_autoRefresh)
		{
			refreshAuthToken();
		}
		else
		{
			setTokenInfo(QString::null, QString::null, QDateTime());
		}
	}
	return IS_LOGGED_IN;
}

bool OAuthLoginControl::loggedIn() const
{
	return IS_LOGGED_IN;
}

QString OAuthLoginControl::token() const
{
	return _token;
}

void OAuthLoginControl::setToken(QString token)
{
	setTokenInfo(token, _refreshToken, _tokenExpire);
}

QString OAuthLoginControl::refreshToken() const
{
	return _refreshToken;
}

void OAuthLoginControl::setRefreshToken(QString refreshToken)
{
	setTokenInfo(_token, refreshToken, _tokenExpire);
}

QDateTime OAuthLoginControl::tokenExpireDateTime() const
{
	return _tokenExpire;
}

void OAuthLoginControl::setTokenExpireDateTime(QDateTime tokenExpireDateTime)
{
	setTokenInfo(_token, _refreshToken, tokenExpireDateTime);
}

bool OAuthLoginControl::autoRefreshToken() const
{
	return _autoRefresh;
}

void OAuthLoginControl::setAutoRefreshToken(bool autoRefresh)
{
	if(_autoRefresh != autoRefresh)
	{
		_autoRefresh = autoRefresh;
		emit autoRefreshChanged(autoRefresh);

		if(_tokenExpire.isValid() && _tokenExpire <= QDateTime::currentDateTimeUtc())
		{
			refreshAuthToken();
		}
	}
}

QNetworkRequest* OAuthLoginControl::generateNetworkRequest() const
{
	if(IS_LOGGED_IN)
	{
		QNetworkRequest* request = new QNetworkRequest();
		request->setRawHeader(QByteArray("Authorization"), _token.toAscii());
		return request;
	}
	return NULL;
}

void OAuthLoginControl::setTokenInfo(QString token, QString refreshToken, QDateTime expireDateTime)
{
	bool loggedIn = IS_LOGGED_IN;
	if(_token != token)
	{
		_token = token;
		emit tokenChanged(token);
	}
	if(_refreshToken != refreshToken)
	{
		_refreshToken = refreshToken;
		emit refreshTokenChanged(refreshToken);
	}
	if(_tokenExpire != expireDateTime)
	{
		_tokenExpire = expireDateTime;
		emit tokenExpireDateTimeChanged(expireDateTime);
	}
	bool newLoggedIn = IS_LOGGED_IN;
	if(loggedIn != newLoggedIn)
	{
		emit loggedInChanged(newLoggedIn);
	}
}

void OAuthLoginControl::login(QString state)
{
	if(!running)
	{
		QUrl url = QUrl(_authUrl);
		url.addQueryItem("redirect_uri", _redirectUrl.toString());
		url.addQueryItem("response_type", "code");
		url.addQueryItem("client_id", _clientId);
		url.addQueryItem("scope", _scope);
		if(!(state.isNull() || state.isEmpty()))
		{
			url.addQueryItem("state", state);
		}

		_browser->setUrl(url);

		running = true;
		emit loginStarted(state);
	}
}

void OAuthLoginControl::refreshAuthToken()
{
	if(!running)
	{
		QUrl postData;
		postData.addQueryItem("client_id", _clientId);
		postData.addQueryItem("client_secret", _clientSecret);
		postData.addQueryItem("grant_type", "refresh_token");
		postData.addQueryItem("refresh_token", _refreshToken);
		postData.addQueryItem("redirect_uri", _redirectUrl.toString());
		if(!(_scope.isNull() || _scope.isEmpty()))
		{
			postData.addQueryItem("scope", _scope);
		}

		QNetworkRequest request = QNetworkRequest();
		request.setUrl(_authRefreshUrl);
		request.setHeader(QNetworkRequest::ContentTypeHeader, "application/x-www-form-urlencoded");

		running = true;
		_netAccess->post(request, postData.encodedQuery());
	}
}

void OAuthLoginControl::cancelLogin()
{
	if(running)
	{
		running = false;
		if(_browser->url() != _defUrl)
		{
			_browser->setUrl(_defUrl);
		}
		emit loginCanceled();
		emit loginComplete();
	}
}

void OAuthLoginControl::logout()
{
	setTokenInfo(QString::null, QString::null, QDateTime());
}

void OAuthLoginControl::verifyLogin(WebNavigationRequest* request)
{
	if(running)
	{
		QUrl url = request->url();
		if(url.host() == _redirectUrl.host() && url.path() == _redirectUrl.path())
		{
			request->ignore();

			QString state = url.queryItemValue("state");
			QString result = url.queryItemValue("error");
			if(result.isNull() || result.isEmpty())
			{
				result = url.queryItemValue("code");

				QUrl postData;
				postData.addQueryItem("client_id", _clientId);
				postData.addQueryItem("client_secret", _clientSecret);
				postData.addQueryItem("grant_type", "authorization_code");
				postData.addQueryItem("code", result);
				postData.addQueryItem("redirect_uri", _redirectUrl.toString());
				if(!(_scope.isNull() || _scope.isEmpty()))
				{
					postData.addQueryItem("scope", _scope);
				}

				QNetworkRequest request = QNetworkRequest();
				request.setUrl(_authRefreshUrl);
				request.setHeader(QNetworkRequest::ContentTypeHeader, "application/x-www-form-urlencoded");

				_netAccess->post(request, postData.encodedQuery());

				emit awaitingLoginReply(state);
			}
			else
			{
				running = false;
				if(url.hasQueryItem("error_description"))
				{
					result = url.queryItemValue("error_description");
				}
				emit loginFailed(result, state);
				emit loginComplete();
			}
		}
	}
}

void OAuthLoginControl::browserMinContentScaleChanged(float minScale)
{
	_sv->scrollViewProperties()->setMinContentScale(minScale);
}

void OAuthLoginControl::browserMaxContentScaleChanged(float maxScale)
{
	_sv->scrollViewProperties()->setMaxContentScale(maxScale);
}

void OAuthLoginControl::onLoginRequestFinished(QNetworkReply* reply)
{
	if(reply)
	{
		if(running)
		{
			QString state = QString::null; //XXX Is there any way for us to get state without saving it?

			if(reply->error() == QNetworkReply::NoError)
			{
				bb::data::JsonDataAccess jda;
				QVariant data = jda.loadFromBuffer(reply->readAll());
				if(jda.hasError())
				{
					emit loginFailed("JSON failed to load: " + jda.error().errorMessage(), state);
				}
				else
				{
					QVariantMap tokenData = data.toMap();

					//Get token
					QString accessToken = tokenData["access_token"].toString();
					QString refreshToken = tokenData["refresh_token"].toString();

					QDateTime now = QDateTime::currentDateTimeUtc();
					QVariant expires = tokenData["expires_in"];
					if(expires.type() == QVariant::LongLong)
					{
						now = now.addSecs((int)expires.toLongLong());
					}
					else if(expires.type() == QVariant::ULongLong)
					{
						now = now.addSecs((int)expires.toULongLong());
					}
					else
					{
						QString error = "Expire timeout is unknown type: " + QString::fromAscii(expires.typeName()) + ". Expire token is set to current time";
						emit loginFailed(error, state);
					}

					//Save info
					setTokenInfo(accessToken, refreshToken, now);
				}
			}
			else
			{
				emit loginFailed("Request error: " + reply->errorString(), state);
			}
			running = false;
			emit loginComplete();
		}
		reply->deleteLater();
	}
}

