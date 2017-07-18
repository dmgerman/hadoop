begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.conf
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
package|;
end_package

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
name|StringEscapeUtils
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Enumeration
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
name|PrintWriter
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletException
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
name|HttpServlet
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
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
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
name|util
operator|.
name|StringUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * A servlet for changing a node's configuration.  *  * Reloads the configuration file, verifies whether changes are  * possible and asks the admin to approve the change.  *  */
end_comment

begin_class
DECL|class|ReconfigurationServlet
specifier|public
class|class
name|ReconfigurationServlet
extends|extends
name|HttpServlet
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ReconfigurationServlet
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// the prefix used to fing the attribute holding the reconfigurable
comment|// for a given request
comment|//
comment|// we get the attribute prefix + servlet path
DECL|field|CONF_SERVLET_RECONFIGURABLE_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|CONF_SERVLET_RECONFIGURABLE_PREFIX
init|=
literal|"conf.servlet.reconfigurable."
decl_stmt|;
annotation|@
name|Override
DECL|method|init ()
specifier|public
name|void
name|init
parameter_list|()
throws|throws
name|ServletException
block|{
name|super
operator|.
name|init
argument_list|()
expr_stmt|;
block|}
DECL|method|getReconfigurable (HttpServletRequest req)
specifier|private
name|Reconfigurable
name|getReconfigurable
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"servlet path: "
operator|+
name|req
operator|.
name|getServletPath
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"getting attribute: "
operator|+
name|CONF_SERVLET_RECONFIGURABLE_PREFIX
operator|+
name|req
operator|.
name|getServletPath
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|(
name|Reconfigurable
operator|)
name|this
operator|.
name|getServletContext
argument_list|()
operator|.
name|getAttribute
argument_list|(
name|CONF_SERVLET_RECONFIGURABLE_PREFIX
operator|+
name|req
operator|.
name|getServletPath
argument_list|()
argument_list|)
return|;
block|}
DECL|method|printHeader (PrintWriter out, String nodeName)
specifier|private
name|void
name|printHeader
parameter_list|(
name|PrintWriter
name|out
parameter_list|,
name|String
name|nodeName
parameter_list|)
block|{
name|out
operator|.
name|print
argument_list|(
literal|"<html><head>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|printf
argument_list|(
literal|"<title>%s Reconfiguration Utility</title>%n"
argument_list|,
name|StringEscapeUtils
operator|.
name|escapeHtml
argument_list|(
name|nodeName
argument_list|)
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"</head><body>\n"
argument_list|)
expr_stmt|;
name|out
operator|.
name|printf
argument_list|(
literal|"<h1>%s Reconfiguration Utility</h1>%n"
argument_list|,
name|StringEscapeUtils
operator|.
name|escapeHtml
argument_list|(
name|nodeName
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|printFooter (PrintWriter out)
specifier|private
name|void
name|printFooter
parameter_list|(
name|PrintWriter
name|out
parameter_list|)
block|{
name|out
operator|.
name|print
argument_list|(
literal|"</body></html>\n"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Print configuration options that can be changed.    */
DECL|method|printConf (PrintWriter out, Reconfigurable reconf)
specifier|private
name|void
name|printConf
parameter_list|(
name|PrintWriter
name|out
parameter_list|,
name|Reconfigurable
name|reconf
parameter_list|)
block|{
name|Configuration
name|oldConf
init|=
name|reconf
operator|.
name|getConf
argument_list|()
decl_stmt|;
name|Configuration
name|newConf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|Collection
argument_list|<
name|ReconfigurationUtil
operator|.
name|PropertyChange
argument_list|>
name|changes
init|=
name|ReconfigurationUtil
operator|.
name|getChangedProperties
argument_list|(
name|newConf
argument_list|,
name|oldConf
argument_list|)
decl_stmt|;
name|boolean
name|changeOK
init|=
literal|true
decl_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<form action=\"\" method=\"post\">"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<table border=\"1\">"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<tr><th>Property</th><th>Old value</th>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<th>New value</th><th></th></tr>"
argument_list|)
expr_stmt|;
for|for
control|(
name|ReconfigurationUtil
operator|.
name|PropertyChange
name|c
range|:
name|changes
control|)
block|{
name|out
operator|.
name|print
argument_list|(
literal|"<tr><td>"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|reconf
operator|.
name|isPropertyReconfigurable
argument_list|(
name|c
operator|.
name|prop
argument_list|)
condition|)
block|{
name|out
operator|.
name|print
argument_list|(
literal|"<font color=\"red\">"
operator|+
name|StringEscapeUtils
operator|.
name|escapeHtml
argument_list|(
name|c
operator|.
name|prop
argument_list|)
operator|+
literal|"</font>"
argument_list|)
expr_stmt|;
name|changeOK
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|print
argument_list|(
name|StringEscapeUtils
operator|.
name|escapeHtml
argument_list|(
name|c
operator|.
name|prop
argument_list|)
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"<input type=\"hidden\" name=\""
operator|+
name|StringEscapeUtils
operator|.
name|escapeHtml
argument_list|(
name|c
operator|.
name|prop
argument_list|)
operator|+
literal|"\" value=\""
operator|+
name|StringEscapeUtils
operator|.
name|escapeHtml
argument_list|(
name|c
operator|.
name|newVal
argument_list|)
operator|+
literal|"\"/>"
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|print
argument_list|(
literal|"</td><td>"
operator|+
operator|(
name|c
operator|.
name|oldVal
operator|==
literal|null
condition|?
literal|"<it>default</it>"
else|:
name|StringEscapeUtils
operator|.
name|escapeHtml
argument_list|(
name|c
operator|.
name|oldVal
argument_list|)
operator|)
operator|+
literal|"</td><td>"
operator|+
operator|(
name|c
operator|.
name|newVal
operator|==
literal|null
condition|?
literal|"<it>default</it>"
else|:
name|StringEscapeUtils
operator|.
name|escapeHtml
argument_list|(
name|c
operator|.
name|newVal
argument_list|)
operator|)
operator|+
literal|"</td>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"</tr>\n"
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|println
argument_list|(
literal|"</table>"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|changeOK
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"<p><font color=\"red\">WARNING: properties marked red"
operator|+
literal|" will not be changed until the next restart.</font></p>"
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|println
argument_list|(
literal|"<input type=\"submit\" value=\"Apply\" />"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"</form>"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getParams (HttpServletRequest req)
specifier|private
name|Enumeration
argument_list|<
name|String
argument_list|>
name|getParams
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|)
block|{
return|return
name|req
operator|.
name|getParameterNames
argument_list|()
return|;
block|}
comment|/**    * Apply configuratio changes after admin has approved them.    */
DECL|method|applyChanges (PrintWriter out, Reconfigurable reconf, HttpServletRequest req)
specifier|private
name|void
name|applyChanges
parameter_list|(
name|PrintWriter
name|out
parameter_list|,
name|Reconfigurable
name|reconf
parameter_list|,
name|HttpServletRequest
name|req
parameter_list|)
throws|throws
name|ReconfigurationException
block|{
name|Configuration
name|oldConf
init|=
name|reconf
operator|.
name|getConf
argument_list|()
decl_stmt|;
name|Configuration
name|newConf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|Enumeration
argument_list|<
name|String
argument_list|>
name|params
init|=
name|getParams
argument_list|(
name|req
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|oldConf
init|)
block|{
while|while
condition|(
name|params
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|String
name|rawParam
init|=
name|params
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|String
name|param
init|=
name|StringEscapeUtils
operator|.
name|unescapeHtml
argument_list|(
name|rawParam
argument_list|)
decl_stmt|;
name|String
name|value
init|=
name|StringEscapeUtils
operator|.
name|unescapeHtml
argument_list|(
name|req
operator|.
name|getParameter
argument_list|(
name|rawParam
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|value
operator|.
name|equals
argument_list|(
name|newConf
operator|.
name|getRaw
argument_list|(
name|param
argument_list|)
argument_list|)
operator|||
name|value
operator|.
name|equals
argument_list|(
literal|"default"
argument_list|)
operator|||
name|value
operator|.
name|equals
argument_list|(
literal|"null"
argument_list|)
operator|||
name|value
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
operator|(
name|value
operator|.
name|equals
argument_list|(
literal|"default"
argument_list|)
operator|||
name|value
operator|.
name|equals
argument_list|(
literal|"null"
argument_list|)
operator|||
name|value
operator|.
name|isEmpty
argument_list|()
operator|)
operator|&&
name|oldConf
operator|.
name|getRaw
argument_list|(
name|param
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"<p>Changed \""
operator|+
name|StringEscapeUtils
operator|.
name|escapeHtml
argument_list|(
name|param
argument_list|)
operator|+
literal|"\" from \""
operator|+
name|StringEscapeUtils
operator|.
name|escapeHtml
argument_list|(
name|oldConf
operator|.
name|getRaw
argument_list|(
name|param
argument_list|)
argument_list|)
operator|+
literal|"\" to default</p>"
argument_list|)
expr_stmt|;
name|reconf
operator|.
name|reconfigureProperty
argument_list|(
name|param
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|value
operator|.
name|equals
argument_list|(
literal|"default"
argument_list|)
operator|&&
operator|!
name|value
operator|.
name|equals
argument_list|(
literal|"null"
argument_list|)
operator|&&
operator|!
name|value
operator|.
name|isEmpty
argument_list|()
operator|&&
operator|(
name|oldConf
operator|.
name|getRaw
argument_list|(
name|param
argument_list|)
operator|==
literal|null
operator|||
operator|!
name|oldConf
operator|.
name|getRaw
argument_list|(
name|param
argument_list|)
operator|.
name|equals
argument_list|(
name|value
argument_list|)
operator|)
condition|)
block|{
comment|// change from default or value to different value
if|if
condition|(
name|oldConf
operator|.
name|getRaw
argument_list|(
name|param
argument_list|)
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"<p>Changed \""
operator|+
name|StringEscapeUtils
operator|.
name|escapeHtml
argument_list|(
name|param
argument_list|)
operator|+
literal|"\" from default to \""
operator|+
name|StringEscapeUtils
operator|.
name|escapeHtml
argument_list|(
name|value
argument_list|)
operator|+
literal|"\"</p>"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|println
argument_list|(
literal|"<p>Changed \""
operator|+
name|StringEscapeUtils
operator|.
name|escapeHtml
argument_list|(
name|param
argument_list|)
operator|+
literal|"\" from \""
operator|+
name|StringEscapeUtils
operator|.
name|escapeHtml
argument_list|(
name|oldConf
operator|.
name|getRaw
argument_list|(
name|param
argument_list|)
argument_list|)
operator|+
literal|"\" to \""
operator|+
name|StringEscapeUtils
operator|.
name|escapeHtml
argument_list|(
name|value
argument_list|)
operator|+
literal|"\"</p>"
argument_list|)
expr_stmt|;
block|}
name|reconf
operator|.
name|reconfigureProperty
argument_list|(
name|param
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"property "
operator|+
name|param
operator|+
literal|" unchanged"
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// parameter value != newConf value
name|out
operator|.
name|println
argument_list|(
literal|"<p>\""
operator|+
name|StringEscapeUtils
operator|.
name|escapeHtml
argument_list|(
name|param
argument_list|)
operator|+
literal|"\" not changed because value has changed from \""
operator|+
name|StringEscapeUtils
operator|.
name|escapeHtml
argument_list|(
name|value
argument_list|)
operator|+
literal|"\" to \""
operator|+
name|StringEscapeUtils
operator|.
name|escapeHtml
argument_list|(
name|newConf
operator|.
name|getRaw
argument_list|(
name|param
argument_list|)
argument_list|)
operator|+
literal|"\" since approval</p>"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|doGet (HttpServletRequest req, HttpServletResponse resp)
specifier|protected
name|void
name|doGet
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|,
name|HttpServletResponse
name|resp
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"GET"
argument_list|)
expr_stmt|;
name|resp
operator|.
name|setContentType
argument_list|(
literal|"text/html"
argument_list|)
expr_stmt|;
name|PrintWriter
name|out
init|=
name|resp
operator|.
name|getWriter
argument_list|()
decl_stmt|;
name|Reconfigurable
name|reconf
init|=
name|getReconfigurable
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|String
name|nodeName
init|=
name|reconf
operator|.
name|getClass
argument_list|()
operator|.
name|getCanonicalName
argument_list|()
decl_stmt|;
name|printHeader
argument_list|(
name|out
argument_list|,
name|nodeName
argument_list|)
expr_stmt|;
name|printConf
argument_list|(
name|out
argument_list|,
name|reconf
argument_list|)
expr_stmt|;
name|printFooter
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doPost (HttpServletRequest req, HttpServletResponse resp)
specifier|protected
name|void
name|doPost
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|,
name|HttpServletResponse
name|resp
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"POST"
argument_list|)
expr_stmt|;
name|resp
operator|.
name|setContentType
argument_list|(
literal|"text/html"
argument_list|)
expr_stmt|;
name|PrintWriter
name|out
init|=
name|resp
operator|.
name|getWriter
argument_list|()
decl_stmt|;
name|Reconfigurable
name|reconf
init|=
name|getReconfigurable
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|String
name|nodeName
init|=
name|reconf
operator|.
name|getClass
argument_list|()
operator|.
name|getCanonicalName
argument_list|()
decl_stmt|;
name|printHeader
argument_list|(
name|out
argument_list|,
name|nodeName
argument_list|)
expr_stmt|;
try|try
block|{
name|applyChanges
argument_list|(
name|out
argument_list|,
name|reconf
argument_list|,
name|req
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ReconfigurationException
name|e
parameter_list|)
block|{
name|resp
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_INTERNAL_SERVER_ERROR
argument_list|,
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
name|out
operator|.
name|println
argument_list|(
literal|"<p><a href=\""
operator|+
name|req
operator|.
name|getServletPath
argument_list|()
operator|+
literal|"\">back</a></p>"
argument_list|)
expr_stmt|;
name|printFooter
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

