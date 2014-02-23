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

#ifndef OAUTHLOGINCONTROL_H_
#define OAUTHLOGINCONTROL_H_

#include <bb/cascades/CustomControl>

#include <QDateTime>
#include <QUrl>

namespace bb
{
    namespace cascades
    {
        class WebView;
        class WebNavigationRequest;
        class ScrollView;
    }
}

class QNetworkRequest;
class QNetworkAccessManager;

namespace rebuild
{
	namespace ui
	{
		namespace component
		{
			class OAuthLoginControl : public bb::cascades::CustomControl
			{
				Q_OBJECT

				Q_PROPERTY(QUrl defaultUrl READ defaultUrl WRITE setDefaultUrl NOTIFY defaultUrlChanged FINAL)
				Q_PROPERTY(QUrl authUrl READ authUrl WRITE setAuthUrl NOTIFY authUrlChanged FINAL)
				Q_PROPERTY(QUrl tokenUrl READ tokenUrl WRITE setTokenUrl NOTIFY tokenUrlChanged FINAL)
				Q_PROPERTY(QUrl redirectUrl READ redirectUrl WRITE setRedirectUrl NOTIFY redirectUrlChanged FINAL)

				Q_PROPERTY(QString clientId READ clientId WRITE setClientId NOTIFY clientIdChanged FINAL)
				Q_PROPERTY(QString clientSecret READ clientSecret WRITE setClientSecret NOTIFY clientSecretChanged FINAL)
				Q_PROPERTY(QString scope READ scope WRITE setScope NOTIFY scopeChanged FINAL)

				Q_PROPERTY(bool loggedIn READ loggedIn NOTIFY loggedInChanged FINAL)

				Q_PROPERTY(QString token READ token NOTIFY tokenChanged FINAL)
				Q_PROPERTY(QString refreshToken READ refreshToken NOTIFY refreshTokenChanged FINAL)
				Q_PROPERTY(QDateTime tokenExpireDateTime READ tokenExpireDateTime NOTIFY tokenExpireDateTimeChanged FINAL)

				Q_PROPERTY(bool autoRefreshToken READ autoRefreshToken WRITE setAutoRefreshToken NOTIFY autoRefreshChanged FINAL)

			public:
				OAuthLoginControl(bb::cascades::Container* parent = 0);
				virtual ~OAuthLoginControl();

				QUrl defaultUrl() const;
				void setDefaultUrl(QUrl url);
				QUrl authUrl() const;
				void setAuthUrl(QUrl url);
				QUrl tokenUrl() const;
				void setTokenUrl(QUrl url);
				QUrl redirectUrl() const;
				void setRedirectUrl(QUrl url);

				QString clientId() const;
				void setClientId(QString clientId);
				QString clientSecret() const;
				void setClientSecret(QString clientSecret);
				QString scope() const;
				void setScope(QString scope);

				bool validateAndCheckLoggedIn();
				bool loggedIn() const;
				QString token() const;
				void setToken(QString token);
				QString refreshToken() const;
				void setRefreshToken(QString refreshToken);
				QDateTime tokenExpireDateTime() const;
				void setTokenExpireDateTime(QDateTime tokenExpireDateTime);

				bool autoRefreshToken() const;
				void setAutoRefreshToken(bool autoRefresh);

				Q_INVOKABLE QNetworkRequest* generateNetworkRequest() const;

			public Q_SLOTS:
				void login(QString state = QString::null);
				void refreshAuthToken();
				void cancelLogin();
				void logout();

			Q_SIGNALS:
				void defaultUrlChanged(QUrl defaultUrl);
				void authUrlChanged(QUrl authUrl);
				void tokenUrlChanged(QUrl authUrl);
				void redirectUrlChanged(QUrl redirectUrl);

				void clientIdChanged(QString clientId);
				void clientSecretChanged(QString clientSecret);
				void scopeChanged(QString scope);

				void loggedInChanged(bool loggedIn);
				void tokenChanged(QString token);
				void refreshTokenChanged(QString refreshToken);
				void tokenExpireDateTimeChanged(QDateTime expireDateTime);

				void autoRefreshChanged(bool autoRefresh);

				void loginStarted(QString state);
				void loginComplete();
				void loginFailed(QString reason, QString state);
				void loginCanceled();
				void awaitingLoginReply(QString state);

				/*! @cond PRIVATE */
			private Q_SLOTS:
				void verifyLogin(bb::cascades::WebNavigationRequest* request);
				void browserMinContentScaleChanged(float minScale);
				void browserMaxContentScaleChanged(float maxScale);
				void onLoginRequestFinished(QNetworkReply* reply);

			private:
				Q_DISABLE_COPY(OAuthLoginControl)

				void setTokenInfo(QString token, QString refreshToken, QDateTime expireDateTime);

				QString _token;
				QString _refreshToken;
				QDateTime _tokenExpire;

				QString _clientId;
				QString _clientSecret;
				QString _scope;

				bool _autoRefresh;
				QUrl _defUrl;
				QUrl _authUrl;
				QUrl _authRefreshUrl;
				QUrl _redirectUrl;

				QNetworkAccessManager* _netAccess;
				bool running;

				bb::cascades::WebView* _browser;
				bb::cascades::ScrollView* _sv;
				/*! @endcond */
			};
		}
	}
}
QML_DECLARE_TYPE(rebuild::ui::component::OAuthLoginControl)

#endif /* OAUTHLOGINCONTROL_H_ */
