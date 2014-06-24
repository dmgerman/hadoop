begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.http.client
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|http
operator|.
name|client
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
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
name|json
operator|.
name|simple
operator|.
name|JSONObject
import|;
end_import

begin_import
import|import
name|org
operator|.
name|json
operator|.
name|simple
operator|.
name|parser
operator|.
name|JSONParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|json
operator|.
name|simple
operator|.
name|parser
operator|.
name|ParseException
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
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Constructor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|HttpURLConnection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URLEncoder
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|MessageFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * Utility methods used by HttpFS classes.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|HttpFSUtils
specifier|public
class|class
name|HttpFSUtils
block|{
DECL|field|SERVICE_NAME
specifier|public
specifier|static
specifier|final
name|String
name|SERVICE_NAME
init|=
literal|"/webhdfs"
decl_stmt|;
DECL|field|SERVICE_VERSION
specifier|public
specifier|static
specifier|final
name|String
name|SERVICE_VERSION
init|=
literal|"/v1"
decl_stmt|;
DECL|field|SERVICE_PATH
specifier|private
specifier|static
specifier|final
name|String
name|SERVICE_PATH
init|=
name|SERVICE_NAME
operator|+
name|SERVICE_VERSION
decl_stmt|;
comment|/**    * Convenience method that creates an HTTP<code>URL</code> for the    * HttpFSServer file system operations.    *<p/>    *    * @param path the file path.    * @param params the query string parameters.    *    * @return a<code>URL</code> for the HttpFSServer server,    *    * @throws IOException thrown if an IO error occurs.    */
DECL|method|createURL (Path path, Map<String, String> params)
specifier|static
name|URL
name|createURL
parameter_list|(
name|Path
name|path
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|createURL
argument_list|(
name|path
argument_list|,
name|params
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**    * Convenience method that creates an HTTP<code>URL</code> for the    * HttpFSServer file system operations.    *<p/>    *    * @param path the file path.    * @param params the query string parameters.    * @param multiValuedParams multi valued parameters of the query string    *    * @return URL a<code>URL</code> for the HttpFSServer server,    *    * @throws IOException thrown if an IO error occurs.    */
DECL|method|createURL (Path path, Map<String, String> params, Map<String, List<String>> multiValuedParams)
specifier|static
name|URL
name|createURL
parameter_list|(
name|Path
name|path
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|multiValuedParams
parameter_list|)
throws|throws
name|IOException
block|{
name|URI
name|uri
init|=
name|path
operator|.
name|toUri
argument_list|()
decl_stmt|;
name|String
name|realScheme
decl_stmt|;
if|if
condition|(
name|uri
operator|.
name|getScheme
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
name|HttpFSFileSystem
operator|.
name|SCHEME
argument_list|)
condition|)
block|{
name|realScheme
operator|=
literal|"http"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|uri
operator|.
name|getScheme
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
name|HttpsFSFileSystem
operator|.
name|SCHEME
argument_list|)
condition|)
block|{
name|realScheme
operator|=
literal|"https"
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"Invalid scheme [{0}] it should be '"
operator|+
name|HttpFSFileSystem
operator|.
name|SCHEME
operator|+
literal|"' "
operator|+
literal|"or '"
operator|+
name|HttpsFSFileSystem
operator|.
name|SCHEME
operator|+
literal|"'"
argument_list|,
name|uri
argument_list|)
argument_list|)
throw|;
block|}
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|realScheme
argument_list|)
operator|.
name|append
argument_list|(
literal|"://"
argument_list|)
operator|.
name|append
argument_list|(
name|uri
operator|.
name|getAuthority
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|SERVICE_PATH
argument_list|)
operator|.
name|append
argument_list|(
name|uri
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|separator
init|=
literal|"?"
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|params
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|separator
argument_list|)
operator|.
name|append
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"="
argument_list|)
operator|.
name|append
argument_list|(
name|URLEncoder
operator|.
name|encode
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|,
literal|"UTF8"
argument_list|)
argument_list|)
expr_stmt|;
name|separator
operator|=
literal|"&"
expr_stmt|;
block|}
if|if
condition|(
name|multiValuedParams
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|multiValuedEntry
range|:
name|multiValuedParams
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|URLEncoder
operator|.
name|encode
argument_list|(
name|multiValuedEntry
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"UTF8"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|values
init|=
name|multiValuedEntry
operator|.
name|getValue
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|value
range|:
name|values
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|separator
argument_list|)
operator|.
name|append
argument_list|(
name|name
argument_list|)
operator|.
name|append
argument_list|(
literal|"="
argument_list|)
operator|.
name|append
argument_list|(
name|URLEncoder
operator|.
name|encode
argument_list|(
name|value
argument_list|,
literal|"UTF8"
argument_list|)
argument_list|)
expr_stmt|;
name|separator
operator|=
literal|"&"
expr_stmt|;
block|}
block|}
block|}
return|return
operator|new
name|URL
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Validates the status of an<code>HttpURLConnection</code> against an    * expected HTTP status code. If the current status code is not the expected    * one it throws an exception with a detail message using Server side error    * messages if available.    *    * @param conn the<code>HttpURLConnection</code>.    * @param expected the expected HTTP status code.    *    * @throws IOException thrown if the current status code does not match the    * expected one.    */
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|}
argument_list|)
DECL|method|validateResponse (HttpURLConnection conn, int expected)
specifier|static
name|void
name|validateResponse
parameter_list|(
name|HttpURLConnection
name|conn
parameter_list|,
name|int
name|expected
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|status
init|=
name|conn
operator|.
name|getResponseCode
argument_list|()
decl_stmt|;
if|if
condition|(
name|status
operator|!=
name|expected
condition|)
block|{
try|try
block|{
name|JSONObject
name|json
init|=
operator|(
name|JSONObject
operator|)
name|HttpFSUtils
operator|.
name|jsonParse
argument_list|(
name|conn
argument_list|)
decl_stmt|;
name|json
operator|=
operator|(
name|JSONObject
operator|)
name|json
operator|.
name|get
argument_list|(
name|HttpFSFileSystem
operator|.
name|ERROR_JSON
argument_list|)
expr_stmt|;
name|String
name|message
init|=
operator|(
name|String
operator|)
name|json
operator|.
name|get
argument_list|(
name|HttpFSFileSystem
operator|.
name|ERROR_MESSAGE_JSON
argument_list|)
decl_stmt|;
name|String
name|exception
init|=
operator|(
name|String
operator|)
name|json
operator|.
name|get
argument_list|(
name|HttpFSFileSystem
operator|.
name|ERROR_EXCEPTION_JSON
argument_list|)
decl_stmt|;
name|String
name|className
init|=
operator|(
name|String
operator|)
name|json
operator|.
name|get
argument_list|(
name|HttpFSFileSystem
operator|.
name|ERROR_CLASSNAME_JSON
argument_list|)
decl_stmt|;
try|try
block|{
name|ClassLoader
name|cl
init|=
name|HttpFSFileSystem
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
decl_stmt|;
name|Class
name|klass
init|=
name|cl
operator|.
name|loadClass
argument_list|(
name|className
argument_list|)
decl_stmt|;
name|Constructor
name|constr
init|=
name|klass
operator|.
name|getConstructor
argument_list|(
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
throw|throw
operator|(
name|IOException
operator|)
name|constr
operator|.
name|newInstance
argument_list|(
name|message
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
name|ex
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"{0} - {1}"
argument_list|,
name|exception
argument_list|,
name|message
argument_list|)
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
if|if
condition|(
name|ex
operator|.
name|getCause
argument_list|()
operator|instanceof
name|IOException
condition|)
block|{
throw|throw
operator|(
name|IOException
operator|)
name|ex
operator|.
name|getCause
argument_list|()
throw|;
block|}
throw|throw
operator|new
name|IOException
argument_list|(
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"HTTP status [{0}], {1}"
argument_list|,
name|status
argument_list|,
name|conn
operator|.
name|getResponseMessage
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**    * Convenience method that JSON Parses the<code>InputStream</code> of a    *<code>HttpURLConnection</code>.    *    * @param conn the<code>HttpURLConnection</code>.    *    * @return the parsed JSON object.    *    * @throws IOException thrown if the<code>InputStream</code> could not be    * JSON parsed.    */
DECL|method|jsonParse (HttpURLConnection conn)
specifier|static
name|Object
name|jsonParse
parameter_list|(
name|HttpURLConnection
name|conn
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|JSONParser
name|parser
init|=
operator|new
name|JSONParser
argument_list|()
decl_stmt|;
return|return
name|parser
operator|.
name|parse
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|conn
operator|.
name|getInputStream
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"JSON parser error, "
operator|+
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

