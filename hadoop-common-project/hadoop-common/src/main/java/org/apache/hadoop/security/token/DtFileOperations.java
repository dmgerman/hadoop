begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security.token
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|token
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ServiceLoader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang
operator|.
name|StringUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|Text
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|Credentials
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|token
operator|.
name|delegation
operator|.
name|AbstractDelegationTokenIdentifier
import|;
end_import

begin_comment
comment|/**  * DtFileOperations is a collection of delegation token file operations.  */
end_comment

begin_class
DECL|class|DtFileOperations
specifier|public
specifier|final
class|class
name|DtFileOperations
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|DtFileOperations
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** No public constructor as per checkstyle. */
DECL|method|DtFileOperations ()
specifier|private
name|DtFileOperations
parameter_list|()
block|{ }
comment|/**    * Use FORMAT_* as arguments to format parameters.    * FORMAT_PB is for protobuf output.    */
DECL|field|FORMAT_PB
specifier|public
specifier|static
specifier|final
name|String
name|FORMAT_PB
init|=
literal|"protobuf"
decl_stmt|;
comment|/**    * Use FORMAT_* as arguments to format parameters.    * FORMAT_JAVA is a legacy option for java serialization output.    */
DECL|field|FORMAT_JAVA
specifier|public
specifier|static
specifier|final
name|String
name|FORMAT_JAVA
init|=
literal|"java"
decl_stmt|;
DECL|field|NA_STRING
specifier|private
specifier|static
specifier|final
name|String
name|NA_STRING
init|=
literal|"-NA-"
decl_stmt|;
DECL|field|PREFIX_HTTP
specifier|private
specifier|static
specifier|final
name|String
name|PREFIX_HTTP
init|=
literal|"http://"
decl_stmt|;
DECL|field|PREFIX_HTTPS
specifier|private
specifier|static
specifier|final
name|String
name|PREFIX_HTTPS
init|=
literal|"https://"
decl_stmt|;
comment|/** Let the DtFetcher code add the appropriate prefix if HTTP/S is used. */
DECL|method|stripPrefix (String u)
specifier|private
specifier|static
name|String
name|stripPrefix
parameter_list|(
name|String
name|u
parameter_list|)
block|{
return|return
name|u
operator|.
name|replaceFirst
argument_list|(
name|PREFIX_HTTP
argument_list|,
literal|""
argument_list|)
operator|.
name|replaceFirst
argument_list|(
name|PREFIX_HTTPS
argument_list|,
literal|""
argument_list|)
return|;
block|}
comment|/** Match token service field to alias text.  True if alias is null. */
DECL|method|matchAlias (Token<?> token, Text alias)
specifier|private
specifier|static
name|boolean
name|matchAlias
parameter_list|(
name|Token
argument_list|<
name|?
argument_list|>
name|token
parameter_list|,
name|Text
name|alias
parameter_list|)
block|{
return|return
name|alias
operator|==
literal|null
operator|||
name|token
operator|.
name|getService
argument_list|()
operator|.
name|equals
argument_list|(
name|alias
argument_list|)
return|;
block|}
comment|/** Match fetcher's service name to the service text and/or url prefix. */
DECL|method|matchService ( DtFetcher fetcher, Text service, String url)
specifier|private
specifier|static
name|boolean
name|matchService
parameter_list|(
name|DtFetcher
name|fetcher
parameter_list|,
name|Text
name|service
parameter_list|,
name|String
name|url
parameter_list|)
block|{
name|Text
name|sName
init|=
name|fetcher
operator|.
name|getServiceName
argument_list|()
decl_stmt|;
return|return
operator|(
name|service
operator|==
literal|null
operator|&&
name|url
operator|.
name|startsWith
argument_list|(
name|sName
operator|.
name|toString
argument_list|()
operator|+
literal|"://"
argument_list|)
operator|)
operator|||
operator|(
name|service
operator|!=
literal|null
operator|&&
name|service
operator|.
name|equals
argument_list|(
name|sName
argument_list|)
operator|)
return|;
block|}
comment|/** Format a long integer type into a date string. */
DECL|method|formatDate (long date)
specifier|private
specifier|static
name|String
name|formatDate
parameter_list|(
name|long
name|date
parameter_list|)
block|{
name|DateFormat
name|df
init|=
name|DateFormat
operator|.
name|getDateTimeInstance
argument_list|(
name|DateFormat
operator|.
name|SHORT
argument_list|,
name|DateFormat
operator|.
name|SHORT
argument_list|)
decl_stmt|;
return|return
name|df
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|(
name|date
argument_list|)
argument_list|)
return|;
block|}
comment|/** Add the service prefix for a local filesystem. */
DECL|method|fileToPath (File f)
specifier|private
specifier|static
name|Path
name|fileToPath
parameter_list|(
name|File
name|f
parameter_list|)
block|{
return|return
operator|new
name|Path
argument_list|(
literal|"file:"
operator|+
name|f
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
return|;
block|}
comment|/** Write out a Credentials object as a local file.    *  @param f a local File object.    *  @param format a string equal to FORMAT_PB or FORMAT_JAVA.    *  @param creds the Credentials object to be written out.    *  @param conf a Configuration object passed along.    *  @throws IOException    */
DECL|method|doFormattedWrite ( File f, String format, Credentials creds, Configuration conf)
specifier|public
specifier|static
name|void
name|doFormattedWrite
parameter_list|(
name|File
name|f
parameter_list|,
name|String
name|format
parameter_list|,
name|Credentials
name|creds
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|format
operator|==
literal|null
operator|||
name|format
operator|.
name|equals
argument_list|(
name|FORMAT_PB
argument_list|)
condition|)
block|{
name|creds
operator|.
name|writeTokenStorageFile
argument_list|(
name|fileToPath
argument_list|(
name|f
argument_list|)
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// if (format != null&& format.equals(FORMAT_JAVA)) {
name|creds
operator|.
name|writeLegacyTokenStorageLocalFile
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Print out a Credentials file from the local filesystem.    *  @param tokenFile a local File object.    *  @param alias print only tokens matching alias (null matches all).    *  @param conf Configuration object passed along.    *  @param out print to this stream.    *  @throws IOException    */
DECL|method|printTokenFile ( File tokenFile, Text alias, Configuration conf, PrintStream out)
specifier|public
specifier|static
name|void
name|printTokenFile
parameter_list|(
name|File
name|tokenFile
parameter_list|,
name|Text
name|alias
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|PrintStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|println
argument_list|(
literal|"File: "
operator|+
name|tokenFile
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|Credentials
name|creds
init|=
name|Credentials
operator|.
name|readTokenStorageFile
argument_list|(
name|tokenFile
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|printCredentials
argument_list|(
name|creds
argument_list|,
name|alias
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
comment|/** Print out a Credentials object.    *  @param creds the Credentials object to be printed out.    *  @param alias print only tokens matching alias (null matches all).    *  @param out print to this stream.    *  @throws IOException    */
DECL|method|printCredentials ( Credentials creds, Text alias, PrintStream out)
specifier|public
specifier|static
name|void
name|printCredentials
parameter_list|(
name|Credentials
name|creds
parameter_list|,
name|Text
name|alias
parameter_list|,
name|PrintStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|tokenHeader
init|=
literal|true
decl_stmt|;
name|String
name|fmt
init|=
literal|"%-24s %-20s %-15s %-12s %s%n"
decl_stmt|;
for|for
control|(
name|Token
argument_list|<
name|?
argument_list|>
name|token
range|:
name|creds
operator|.
name|getAllTokens
argument_list|()
control|)
block|{
if|if
condition|(
name|matchAlias
argument_list|(
name|token
argument_list|,
name|alias
argument_list|)
condition|)
block|{
if|if
condition|(
name|tokenHeader
condition|)
block|{
name|out
operator|.
name|printf
argument_list|(
name|fmt
argument_list|,
literal|"Token kind"
argument_list|,
literal|"Service"
argument_list|,
literal|"Renewer"
argument_list|,
literal|"Exp date"
argument_list|,
literal|"URL enc token"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
name|StringUtils
operator|.
name|repeat
argument_list|(
literal|"-"
argument_list|,
literal|80
argument_list|)
argument_list|)
expr_stmt|;
name|tokenHeader
operator|=
literal|false
expr_stmt|;
block|}
name|AbstractDelegationTokenIdentifier
name|id
init|=
operator|(
name|AbstractDelegationTokenIdentifier
operator|)
name|token
operator|.
name|decodeIdentifier
argument_list|()
decl_stmt|;
name|out
operator|.
name|printf
argument_list|(
name|fmt
argument_list|,
name|token
operator|.
name|getKind
argument_list|()
argument_list|,
name|token
operator|.
name|getService
argument_list|()
argument_list|,
operator|(
name|id
operator|!=
literal|null
operator|)
condition|?
name|id
operator|.
name|getRenewer
argument_list|()
else|:
name|NA_STRING
argument_list|,
operator|(
name|id
operator|!=
literal|null
operator|)
condition|?
name|formatDate
argument_list|(
name|id
operator|.
name|getMaxDate
argument_list|()
argument_list|)
else|:
name|NA_STRING
argument_list|,
name|token
operator|.
name|encodeToUrlString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** Fetch a token from a service and save to file in the local filesystem.    *  @param tokenFile a local File object to hold the output.    *  @param fileFormat a string equal to FORMAT_PB or FORMAT_JAVA, for output    *  @param alias overwrite service field of fetched token with this text.    *  @param service use a DtFetcher implementation matching this service text.    *  @param url pass this URL to fetcher after stripping any http/s prefix.    *  @param renewer pass this renewer to the fetcher.    *  @param conf Configuration object passed along.    *  @throws IOException    */
DECL|method|getTokenFile (File tokenFile, String fileFormat, Text alias, Text service, String url, String renewer, Configuration conf)
specifier|public
specifier|static
name|void
name|getTokenFile
parameter_list|(
name|File
name|tokenFile
parameter_list|,
name|String
name|fileFormat
parameter_list|,
name|Text
name|alias
parameter_list|,
name|Text
name|service
parameter_list|,
name|String
name|url
parameter_list|,
name|String
name|renewer
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|Token
argument_list|<
name|?
argument_list|>
name|token
init|=
literal|null
decl_stmt|;
name|Credentials
name|creds
init|=
name|tokenFile
operator|.
name|exists
argument_list|()
condition|?
name|Credentials
operator|.
name|readTokenStorageFile
argument_list|(
name|tokenFile
argument_list|,
name|conf
argument_list|)
else|:
operator|new
name|Credentials
argument_list|()
decl_stmt|;
name|ServiceLoader
argument_list|<
name|DtFetcher
argument_list|>
name|loader
init|=
name|ServiceLoader
operator|.
name|load
argument_list|(
name|DtFetcher
operator|.
name|class
argument_list|)
decl_stmt|;
for|for
control|(
name|DtFetcher
name|fetcher
range|:
name|loader
control|)
block|{
if|if
condition|(
name|matchService
argument_list|(
name|fetcher
argument_list|,
name|service
argument_list|,
name|url
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|fetcher
operator|.
name|isTokenRequired
argument_list|()
condition|)
block|{
name|String
name|message
init|=
literal|"DtFetcher for service '"
operator|+
name|service
operator|+
literal|"' does not require a token.  Check your configuration.  "
operator|+
literal|"Note: security may be disabled or there may be two DtFetcher "
operator|+
literal|"providers for the same service designation."
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|message
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|message
argument_list|)
throw|;
block|}
name|token
operator|=
name|fetcher
operator|.
name|addDelegationTokens
argument_list|(
name|conf
argument_list|,
name|creds
argument_list|,
name|renewer
argument_list|,
name|stripPrefix
argument_list|(
name|url
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|alias
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|token
operator|==
literal|null
condition|)
block|{
name|String
name|message
init|=
literal|"DtFetcher for service '"
operator|+
name|service
operator|+
literal|"'"
operator|+
literal|" does not allow aliasing.  Cannot apply alias '"
operator|+
name|alias
operator|+
literal|"'."
operator|+
literal|"  Drop alias flag to get token for this service."
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|message
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|message
argument_list|)
throw|;
block|}
name|Token
argument_list|<
name|?
argument_list|>
name|aliasedToken
init|=
name|token
operator|.
name|copyToken
argument_list|()
decl_stmt|;
name|aliasedToken
operator|.
name|setService
argument_list|(
name|alias
argument_list|)
expr_stmt|;
name|creds
operator|.
name|addToken
argument_list|(
name|alias
argument_list|,
name|aliasedToken
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Add token with service "
operator|+
name|alias
argument_list|)
expr_stmt|;
block|}
name|doFormattedWrite
argument_list|(
name|tokenFile
argument_list|,
name|fileFormat
argument_list|,
name|creds
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
comment|/** Alias a token from a file and save back to file in the local filesystem.    *  @param tokenFile a local File object to hold the input and output.    *  @param fileFormat a string equal to FORMAT_PB or FORMAT_JAVA, for output    *  @param alias overwrite service field of fetched token with this text.    *  @param service only apply alias to tokens matching this service text.    *  @param conf Configuration object passed along.    *  @throws IOException    */
DECL|method|aliasTokenFile (File tokenFile, String fileFormat, Text alias, Text service, Configuration conf)
specifier|public
specifier|static
name|void
name|aliasTokenFile
parameter_list|(
name|File
name|tokenFile
parameter_list|,
name|String
name|fileFormat
parameter_list|,
name|Text
name|alias
parameter_list|,
name|Text
name|service
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|Credentials
name|newCreds
init|=
operator|new
name|Credentials
argument_list|()
decl_stmt|;
name|Credentials
name|creds
init|=
name|Credentials
operator|.
name|readTokenStorageFile
argument_list|(
name|tokenFile
argument_list|,
name|conf
argument_list|)
decl_stmt|;
for|for
control|(
name|Token
argument_list|<
name|?
argument_list|>
name|token
range|:
name|creds
operator|.
name|getAllTokens
argument_list|()
control|)
block|{
name|newCreds
operator|.
name|addToken
argument_list|(
name|token
operator|.
name|getService
argument_list|()
argument_list|,
name|token
argument_list|)
expr_stmt|;
if|if
condition|(
name|token
operator|.
name|getService
argument_list|()
operator|.
name|equals
argument_list|(
name|service
argument_list|)
condition|)
block|{
name|Token
argument_list|<
name|?
argument_list|>
name|aliasedToken
init|=
name|token
operator|.
name|copyToken
argument_list|()
decl_stmt|;
name|aliasedToken
operator|.
name|setService
argument_list|(
name|alias
argument_list|)
expr_stmt|;
name|newCreds
operator|.
name|addToken
argument_list|(
name|alias
argument_list|,
name|aliasedToken
argument_list|)
expr_stmt|;
block|}
block|}
name|doFormattedWrite
argument_list|(
name|tokenFile
argument_list|,
name|fileFormat
argument_list|,
name|newCreds
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
comment|/** Append tokens from list of files in local filesystem, saving to last file.    *  @param tokenFiles list of local File objects.  Last file holds the output.    *  @param fileFormat a string equal to FORMAT_PB or FORMAT_JAVA, for output    *  @param conf Configuration object passed along.    *  @throws IOException    */
DECL|method|appendTokenFiles ( ArrayList<File> tokenFiles, String fileFormat, Configuration conf)
specifier|public
specifier|static
name|void
name|appendTokenFiles
parameter_list|(
name|ArrayList
argument_list|<
name|File
argument_list|>
name|tokenFiles
parameter_list|,
name|String
name|fileFormat
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|Credentials
name|newCreds
init|=
operator|new
name|Credentials
argument_list|()
decl_stmt|;
name|File
name|lastTokenFile
init|=
literal|null
decl_stmt|;
for|for
control|(
name|File
name|tokenFile
range|:
name|tokenFiles
control|)
block|{
name|lastTokenFile
operator|=
name|tokenFile
expr_stmt|;
name|Credentials
name|creds
init|=
name|Credentials
operator|.
name|readTokenStorageFile
argument_list|(
name|tokenFile
argument_list|,
name|conf
argument_list|)
decl_stmt|;
for|for
control|(
name|Token
argument_list|<
name|?
argument_list|>
name|token
range|:
name|creds
operator|.
name|getAllTokens
argument_list|()
control|)
block|{
name|newCreds
operator|.
name|addToken
argument_list|(
name|token
operator|.
name|getService
argument_list|()
argument_list|,
name|token
argument_list|)
expr_stmt|;
block|}
block|}
name|doFormattedWrite
argument_list|(
name|lastTokenFile
argument_list|,
name|fileFormat
argument_list|,
name|newCreds
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
comment|/** Remove a token from a file in the local filesystem, matching alias.    *  @param cancel cancel token as well as remove from file.    *  @param tokenFile a local File object.    *  @param fileFormat a string equal to FORMAT_PB or FORMAT_JAVA, for output    *  @param alias remove only tokens matching alias; null matches all.    *  @param conf Configuration object passed along.    *  @throws IOException    *  @throws InterruptedException    */
DECL|method|removeTokenFromFile (boolean cancel, File tokenFile, String fileFormat, Text alias, Configuration conf)
specifier|public
specifier|static
name|void
name|removeTokenFromFile
parameter_list|(
name|boolean
name|cancel
parameter_list|,
name|File
name|tokenFile
parameter_list|,
name|String
name|fileFormat
parameter_list|,
name|Text
name|alias
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|Credentials
name|newCreds
init|=
operator|new
name|Credentials
argument_list|()
decl_stmt|;
name|Credentials
name|creds
init|=
name|Credentials
operator|.
name|readTokenStorageFile
argument_list|(
name|tokenFile
argument_list|,
name|conf
argument_list|)
decl_stmt|;
for|for
control|(
name|Token
argument_list|<
name|?
argument_list|>
name|token
range|:
name|creds
operator|.
name|getAllTokens
argument_list|()
control|)
block|{
if|if
condition|(
name|matchAlias
argument_list|(
name|token
argument_list|,
name|alias
argument_list|)
condition|)
block|{
if|if
condition|(
name|token
operator|.
name|isManaged
argument_list|()
operator|&&
name|cancel
condition|)
block|{
name|token
operator|.
name|cancel
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Canceled "
operator|+
name|token
operator|.
name|getKind
argument_list|()
operator|+
literal|":"
operator|+
name|token
operator|.
name|getService
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|newCreds
operator|.
name|addToken
argument_list|(
name|token
operator|.
name|getService
argument_list|()
argument_list|,
name|token
argument_list|)
expr_stmt|;
block|}
block|}
name|doFormattedWrite
argument_list|(
name|tokenFile
argument_list|,
name|fileFormat
argument_list|,
name|newCreds
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
comment|/** Renew a token from a file in the local filesystem, matching alias.    *  @param tokenFile a local File object.    *  @param fileFormat a string equal to FORMAT_PB or FORMAT_JAVA, for output    *  @param alias renew only tokens matching alias; null matches all.    *  @param conf Configuration object passed along.    *  @throws IOException    *  @throws InterruptedException    */
DECL|method|renewTokenFile ( File tokenFile, String fileFormat, Text alias, Configuration conf)
specifier|public
specifier|static
name|void
name|renewTokenFile
parameter_list|(
name|File
name|tokenFile
parameter_list|,
name|String
name|fileFormat
parameter_list|,
name|Text
name|alias
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|Credentials
name|creds
init|=
name|Credentials
operator|.
name|readTokenStorageFile
argument_list|(
name|tokenFile
argument_list|,
name|conf
argument_list|)
decl_stmt|;
for|for
control|(
name|Token
argument_list|<
name|?
argument_list|>
name|token
range|:
name|creds
operator|.
name|getAllTokens
argument_list|()
control|)
block|{
if|if
condition|(
name|token
operator|.
name|isManaged
argument_list|()
operator|&&
name|matchAlias
argument_list|(
name|token
argument_list|,
name|alias
argument_list|)
condition|)
block|{
name|long
name|result
init|=
name|token
operator|.
name|renew
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Renewed"
operator|+
name|token
operator|.
name|getKind
argument_list|()
operator|+
literal|":"
operator|+
name|token
operator|.
name|getService
argument_list|()
operator|+
literal|" until "
operator|+
name|formatDate
argument_list|(
name|result
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|doFormattedWrite
argument_list|(
name|tokenFile
argument_list|,
name|fileFormat
argument_list|,
name|creds
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

