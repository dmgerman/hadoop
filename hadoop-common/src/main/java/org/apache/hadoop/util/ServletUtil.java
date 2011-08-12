begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Calendar
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|*
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
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
name|httpclient
operator|.
name|URIException
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
name|httpclient
operator|.
name|util
operator|.
name|URIUtil
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
name|classification
operator|.
name|InterfaceStability
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
import|;
end_import

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|ServletUtil
specifier|public
class|class
name|ServletUtil
block|{
comment|/**    * Initial HTML header    */
DECL|method|initHTML (ServletResponse response, String title )
specifier|public
specifier|static
name|PrintWriter
name|initHTML
parameter_list|(
name|ServletResponse
name|response
parameter_list|,
name|String
name|title
parameter_list|)
throws|throws
name|IOException
block|{
name|response
operator|.
name|setContentType
argument_list|(
literal|"text/html"
argument_list|)
expr_stmt|;
name|PrintWriter
name|out
init|=
name|response
operator|.
name|getWriter
argument_list|()
decl_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<html>\n"
operator|+
literal|"<link rel='stylesheet' type='text/css' href='/static/hadoop.css'>\n"
operator|+
literal|"<title>"
operator|+
name|title
operator|+
literal|"</title>\n"
operator|+
literal|"<body>\n"
operator|+
literal|"<h1>"
operator|+
name|title
operator|+
literal|"</h1>\n"
argument_list|)
expr_stmt|;
return|return
name|out
return|;
block|}
comment|/**    * Get a parameter from a ServletRequest.    * Return null if the parameter contains only white spaces.    */
DECL|method|getParameter (ServletRequest request, String name)
specifier|public
specifier|static
name|String
name|getParameter
parameter_list|(
name|ServletRequest
name|request
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|String
name|s
init|=
name|request
operator|.
name|getParameter
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|s
operator|=
name|s
operator|.
name|trim
argument_list|()
expr_stmt|;
return|return
name|s
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|?
literal|null
else|:
name|s
return|;
block|}
DECL|field|HTML_TAIL
specifier|public
specifier|static
specifier|final
name|String
name|HTML_TAIL
init|=
literal|"<hr />\n"
operator|+
literal|"<a href='http://hadoop.apache.org/core'>Hadoop</a>, "
operator|+
name|Calendar
operator|.
name|getInstance
argument_list|()
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|YEAR
argument_list|)
operator|+
literal|".\n"
operator|+
literal|"</body></html>"
decl_stmt|;
comment|/**    * HTML footer to be added in the jsps.    * @return the HTML footer.    */
DECL|method|htmlFooter ()
specifier|public
specifier|static
name|String
name|htmlFooter
parameter_list|()
block|{
return|return
name|HTML_TAIL
return|;
block|}
comment|/**    * Generate the percentage graph and returns HTML representation string    * of the same.    *     * @param perc The percentage value for which graph is to be generated    * @param width The width of the display table    * @return HTML String representation of the percentage graph    * @throws IOException    */
DECL|method|percentageGraph (int perc, int width)
specifier|public
specifier|static
name|String
name|percentageGraph
parameter_list|(
name|int
name|perc
parameter_list|,
name|int
name|width
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|perc
operator|>=
literal|0
assert|;
assert|assert
name|perc
operator|<=
literal|100
assert|;
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"<table border=\"1px\" width=\""
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|width
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"px\"><tr>"
argument_list|)
expr_stmt|;
if|if
condition|(
name|perc
operator|>
literal|0
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|"<td cellspacing=\"0\" class=\"perc_filled\" width=\""
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|perc
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"%\"></td>"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|perc
operator|<
literal|100
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|"<td cellspacing=\"0\" class=\"perc_nonfilled\" width=\""
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|100
operator|-
name|perc
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"%\"></td>"
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|append
argument_list|(
literal|"</tr></table>"
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Generate the percentage graph and returns HTML representation string    * of the same.    * @param perc The percentage value for which graph is to be generated    * @param width The width of the display table    * @return HTML String representation of the percentage graph    * @throws IOException    */
DECL|method|percentageGraph (float perc, int width)
specifier|public
specifier|static
name|String
name|percentageGraph
parameter_list|(
name|float
name|perc
parameter_list|,
name|int
name|width
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|percentageGraph
argument_list|(
operator|(
name|int
operator|)
name|perc
argument_list|,
name|width
argument_list|)
return|;
block|}
comment|/**    * Escape and encode a string regarded as within the query component of an URI.    * @param value the value to encode    * @return encoded query, null if the default charset is not supported    */
DECL|method|encodeQueryValue (final String value)
specifier|public
specifier|static
name|String
name|encodeQueryValue
parameter_list|(
specifier|final
name|String
name|value
parameter_list|)
block|{
try|try
block|{
return|return
name|URIUtil
operator|.
name|encodeWithinQuery
argument_list|(
name|value
argument_list|,
literal|"UTF-8"
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|URIException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"JVM does not support UTF-8"
argument_list|)
throw|;
comment|// should never happen!
block|}
block|}
comment|/**    * Escape and encode a string regarded as the path component of an URI.    * @param path the path component to encode    * @return encoded path, null if UTF-8 is not supported    */
DECL|method|encodePath (final String path)
specifier|public
specifier|static
name|String
name|encodePath
parameter_list|(
specifier|final
name|String
name|path
parameter_list|)
block|{
try|try
block|{
return|return
name|URIUtil
operator|.
name|encodePath
argument_list|(
name|path
argument_list|,
literal|"UTF-8"
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|URIException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"JVM does not support UTF-8"
argument_list|)
throw|;
comment|// should never happen!
block|}
block|}
comment|/**    * Parse and decode the path component from the given request.    * @param request Http request to parse    * @param servletName the name of servlet that precedes the path    * @return decoded path component, null if UTF-8 is not supported    */
DECL|method|getDecodedPath (final HttpServletRequest request, String servletName)
specifier|public
specifier|static
name|String
name|getDecodedPath
parameter_list|(
specifier|final
name|HttpServletRequest
name|request
parameter_list|,
name|String
name|servletName
parameter_list|)
block|{
try|try
block|{
return|return
name|URIUtil
operator|.
name|decode
argument_list|(
name|getRawPath
argument_list|(
name|request
argument_list|,
name|servletName
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|URIException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"JVM does not support UTF-8"
argument_list|)
throw|;
comment|// should never happen!
block|}
block|}
comment|/**    * Parse the path component from the given request and return w/o decoding.    * @param request Http request to parse    * @param servletName the name of servlet that precedes the path    * @return path component, null if the default charset is not supported    */
DECL|method|getRawPath (final HttpServletRequest request, String servletName)
specifier|public
specifier|static
name|String
name|getRawPath
parameter_list|(
specifier|final
name|HttpServletRequest
name|request
parameter_list|,
name|String
name|servletName
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|request
operator|.
name|getRequestURI
argument_list|()
operator|.
name|startsWith
argument_list|(
name|servletName
operator|+
literal|"/"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|request
operator|.
name|getRequestURI
argument_list|()
operator|.
name|substring
argument_list|(
name|servletName
operator|.
name|length
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

