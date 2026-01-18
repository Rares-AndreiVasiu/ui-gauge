package com.example.gitgauge.config

object GitHubConfig {

    // The redirect URI must match what you configured in GitHub OAuth App settings
    const val REDIRECT_URI = "gitgauge://oauth-callback"

    // OAuth scopes to request
    const val SCOPES = "read:user user:email public_repo"

    // GitHub OAuth URLs
    const val GITHUB_AUTHORIZE_URL = "https://github.com/login/oauth/authorize"
    const val GITHUB_ACCESS_TOKEN_URL = "https://github.com/login/oauth/access_token"
}
