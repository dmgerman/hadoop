begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *   *      http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.jmx
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|jmx
package|;
end_package

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|core
operator|.
name|JsonFactory
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|core
operator|.
name|JsonGenerator
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
name|http
operator|.
name|HttpServer2
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

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|AttributeNotFoundException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|InstanceNotFoundException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|IntrospectionException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MBeanAttributeInfo
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MBeanException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MBeanInfo
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MBeanServer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MalformedObjectNameException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ObjectName
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ReflectionException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|RuntimeErrorException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|RuntimeMBeanException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|CompositeData
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|CompositeType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|TabularData
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
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|ManagementFactory
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
name|Array
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/*  * This servlet is based off of the JMXProxyServlet from Tomcat 7.0.14. It has  * been rewritten to be read only and to output in a JSON format so it is not  * really that close to the original.  */
end_comment

begin_comment
comment|/**  * Provides Read only web access to JMX.  *<p>  * This servlet generally will be placed under the /jmx URL for each  * HttpServer.  It provides read only  * access to JMX metrics.  The optional<code>qry</code> parameter  * may be used to query only a subset of the JMX Beans.  This query  * functionality is provided through the  * {@link MBeanServer#queryNames(ObjectName, javax.management.QueryExp)}  * method.  *<p>  * For example<code>http://.../jmx?qry=Hadoop:*</code> will return  * all hadoop metrics exposed through JMX.  *<p>  * The optional<code>get</code> parameter is used to query an specific   * attribute of a JMX bean.  The format of the URL is  *<code>http://.../jmx?get=MXBeanName::AttributeName</code>  *<p>  * For example   *<code>  * http://../jmx?get=Hadoop:service=NameNode,name=NameNodeInfo::ClusterId  *</code> will return the cluster id of the namenode mxbean.  *<p>  * If the<code>qry</code> or the<code>get</code> parameter is not formatted   * correctly then a 400 BAD REQUEST http response code will be returned.   *<p>  * If a resouce such as a mbean or attribute can not be found,   * a 404 SC_NOT_FOUND http response code will be returned.   *<p>  * The return format is JSON and in the form  *<p>  *<pre><code>  *  {  *    "beans" : [  *      {  *        "name":"bean-name"  *        ...  *      }  *    ]  *  }  *</code></pre>  *<p>  *  The servlet attempts to convert the the JMXBeans into JSON. Each  *  bean's attributes will be converted to a JSON object member.  *    *  If the attribute is a boolean, a number, a string, or an array  *  it will be converted to the JSON equivalent.   *    *  If the value is a {@link CompositeData} then it will be converted  *  to a JSON object with the keys as the name of the JSON member and  *  the value is converted following these same rules.  *    *  If the value is a {@link TabularData} then it will be converted  *  to an array of the {@link CompositeData} elements that it contains.  *    *  All other objects will be converted to a string and output as such.  *    *  The bean's name and modelerType will be returned for all beans.  *  */
end_comment

begin_class
DECL|class|JMXJsonServlet
specifier|public
class|class
name|JMXJsonServlet
extends|extends
name|HttpServlet
block|{
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
name|JMXJsonServlet
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|ACCESS_CONTROL_ALLOW_METHODS
specifier|static
specifier|final
name|String
name|ACCESS_CONTROL_ALLOW_METHODS
init|=
literal|"Access-Control-Allow-Methods"
decl_stmt|;
DECL|field|ACCESS_CONTROL_ALLOW_ORIGIN
specifier|static
specifier|final
name|String
name|ACCESS_CONTROL_ALLOW_ORIGIN
init|=
literal|"Access-Control-Allow-Origin"
decl_stmt|;
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
comment|/**    * MBean server.    */
DECL|field|mBeanServer
specifier|protected
specifier|transient
name|MBeanServer
name|mBeanServer
decl_stmt|;
comment|/**    * Json Factory to create Json generators for write objects in json format    */
DECL|field|jsonFactory
specifier|protected
specifier|transient
name|JsonFactory
name|jsonFactory
decl_stmt|;
comment|/**    * Initialize this servlet.    */
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
comment|// Retrieve the MBean server
name|mBeanServer
operator|=
name|ManagementFactory
operator|.
name|getPlatformMBeanServer
argument_list|()
expr_stmt|;
name|jsonFactory
operator|=
operator|new
name|JsonFactory
argument_list|()
expr_stmt|;
block|}
DECL|method|isInstrumentationAccessAllowed (HttpServletRequest request, HttpServletResponse response)
specifier|protected
name|boolean
name|isInstrumentationAccessAllowed
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|HttpServer2
operator|.
name|isInstrumentationAccessAllowed
argument_list|(
name|getServletContext
argument_list|()
argument_list|,
name|request
argument_list|,
name|response
argument_list|)
return|;
block|}
comment|/**    * Disable TRACE method to avoid TRACE vulnerability.    */
annotation|@
name|Override
DECL|method|doTrace (HttpServletRequest req, HttpServletResponse resp)
specifier|protected
name|void
name|doTrace
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
name|resp
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_METHOD_NOT_ALLOWED
argument_list|)
expr_stmt|;
block|}
comment|/**    * Process a GET request for the specified resource.    *     * @param request    *          The servlet request we are processing    * @param response    *          The servlet response we are creating    */
annotation|@
name|Override
DECL|method|doGet (HttpServletRequest request, HttpServletResponse response)
specifier|public
name|void
name|doGet
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
operator|!
name|isInstrumentationAccessAllowed
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
condition|)
block|{
return|return;
block|}
name|JsonGenerator
name|jg
init|=
literal|null
decl_stmt|;
name|PrintWriter
name|writer
init|=
literal|null
decl_stmt|;
try|try
block|{
name|writer
operator|=
name|response
operator|.
name|getWriter
argument_list|()
expr_stmt|;
name|response
operator|.
name|setContentType
argument_list|(
literal|"application/json; charset=utf8"
argument_list|)
expr_stmt|;
name|response
operator|.
name|setHeader
argument_list|(
name|ACCESS_CONTROL_ALLOW_METHODS
argument_list|,
literal|"GET"
argument_list|)
expr_stmt|;
name|response
operator|.
name|setHeader
argument_list|(
name|ACCESS_CONTROL_ALLOW_ORIGIN
argument_list|,
literal|"*"
argument_list|)
expr_stmt|;
name|jg
operator|=
name|jsonFactory
operator|.
name|createGenerator
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|jg
operator|.
name|disable
argument_list|(
name|JsonGenerator
operator|.
name|Feature
operator|.
name|AUTO_CLOSE_TARGET
argument_list|)
expr_stmt|;
name|jg
operator|.
name|useDefaultPrettyPrinter
argument_list|()
expr_stmt|;
name|jg
operator|.
name|writeStartObject
argument_list|()
expr_stmt|;
comment|// query per mbean attribute
name|String
name|getmethod
init|=
name|request
operator|.
name|getParameter
argument_list|(
literal|"get"
argument_list|)
decl_stmt|;
if|if
condition|(
name|getmethod
operator|!=
literal|null
condition|)
block|{
name|String
index|[]
name|splitStrings
init|=
name|getmethod
operator|.
name|split
argument_list|(
literal|"\\:\\:"
argument_list|)
decl_stmt|;
if|if
condition|(
name|splitStrings
operator|.
name|length
operator|!=
literal|2
condition|)
block|{
name|jg
operator|.
name|writeStringField
argument_list|(
literal|"result"
argument_list|,
literal|"ERROR"
argument_list|)
expr_stmt|;
name|jg
operator|.
name|writeStringField
argument_list|(
literal|"message"
argument_list|,
literal|"query format is not as expected."
argument_list|)
expr_stmt|;
name|jg
operator|.
name|flush
argument_list|()
expr_stmt|;
name|response
operator|.
name|setStatus
argument_list|(
name|HttpServletResponse
operator|.
name|SC_BAD_REQUEST
argument_list|)
expr_stmt|;
return|return;
block|}
name|listBeans
argument_list|(
name|jg
argument_list|,
operator|new
name|ObjectName
argument_list|(
name|splitStrings
index|[
literal|0
index|]
argument_list|)
argument_list|,
name|splitStrings
index|[
literal|1
index|]
argument_list|,
name|response
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// query per mbean
name|String
name|qry
init|=
name|request
operator|.
name|getParameter
argument_list|(
literal|"qry"
argument_list|)
decl_stmt|;
if|if
condition|(
name|qry
operator|==
literal|null
condition|)
block|{
name|qry
operator|=
literal|"*:*"
expr_stmt|;
block|}
name|listBeans
argument_list|(
name|jg
argument_list|,
operator|new
name|ObjectName
argument_list|(
name|qry
argument_list|)
argument_list|,
literal|null
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|jg
operator|!=
literal|null
condition|)
block|{
name|jg
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|writer
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Caught an exception while processing JMX request"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|response
operator|.
name|setStatus
argument_list|(
name|HttpServletResponse
operator|.
name|SC_INTERNAL_SERVER_ERROR
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MalformedObjectNameException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Caught an exception while processing JMX request"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|response
operator|.
name|setStatus
argument_list|(
name|HttpServletResponse
operator|.
name|SC_BAD_REQUEST
argument_list|)
expr_stmt|;
block|}
block|}
comment|// --------------------------------------------------------- Private Methods
DECL|method|listBeans (JsonGenerator jg, ObjectName qry, String attribute, HttpServletResponse response)
specifier|private
name|void
name|listBeans
parameter_list|(
name|JsonGenerator
name|jg
parameter_list|,
name|ObjectName
name|qry
parameter_list|,
name|String
name|attribute
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Listing beans for "
operator|+
name|qry
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|ObjectName
argument_list|>
name|names
init|=
literal|null
decl_stmt|;
name|names
operator|=
name|mBeanServer
operator|.
name|queryNames
argument_list|(
name|qry
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|jg
operator|.
name|writeArrayFieldStart
argument_list|(
literal|"beans"
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|ObjectName
argument_list|>
name|it
init|=
name|names
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|ObjectName
name|oname
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|MBeanInfo
name|minfo
decl_stmt|;
name|String
name|code
init|=
literal|""
decl_stmt|;
name|Object
name|attributeinfo
init|=
literal|null
decl_stmt|;
try|try
block|{
name|minfo
operator|=
name|mBeanServer
operator|.
name|getMBeanInfo
argument_list|(
name|oname
argument_list|)
expr_stmt|;
name|code
operator|=
name|minfo
operator|.
name|getClassName
argument_list|()
expr_stmt|;
name|String
name|prs
init|=
literal|""
decl_stmt|;
try|try
block|{
if|if
condition|(
literal|"org.apache.commons.modeler.BaseModelMBean"
operator|.
name|equals
argument_list|(
name|code
argument_list|)
condition|)
block|{
name|prs
operator|=
literal|"modelerType"
expr_stmt|;
name|code
operator|=
operator|(
name|String
operator|)
name|mBeanServer
operator|.
name|getAttribute
argument_list|(
name|oname
argument_list|,
name|prs
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|attribute
operator|!=
literal|null
condition|)
block|{
name|prs
operator|=
name|attribute
expr_stmt|;
name|attributeinfo
operator|=
name|mBeanServer
operator|.
name|getAttribute
argument_list|(
name|oname
argument_list|,
name|prs
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|AttributeNotFoundException
name|e
parameter_list|)
block|{
comment|// If the modelerType attribute was not found, the class name is used
comment|// instead.
name|LOG
operator|.
name|error
argument_list|(
literal|"getting attribute "
operator|+
name|prs
operator|+
literal|" of "
operator|+
name|oname
operator|+
literal|" threw an exception"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MBeanException
name|e
parameter_list|)
block|{
comment|// The code inside the attribute getter threw an exception so log it,
comment|// and fall back on the class name
name|LOG
operator|.
name|error
argument_list|(
literal|"getting attribute "
operator|+
name|prs
operator|+
literal|" of "
operator|+
name|oname
operator|+
literal|" threw an exception"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
comment|// For some reason even with an MBeanException available to them
comment|// Runtime exceptionscan still find their way through, so treat them
comment|// the same as MBeanException
name|LOG
operator|.
name|error
argument_list|(
literal|"getting attribute "
operator|+
name|prs
operator|+
literal|" of "
operator|+
name|oname
operator|+
literal|" threw an exception"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ReflectionException
name|e
parameter_list|)
block|{
comment|// This happens when the code inside the JMX bean (setter?? from the
comment|// java docs) threw an exception, so log it and fall back on the
comment|// class name
name|LOG
operator|.
name|error
argument_list|(
literal|"getting attribute "
operator|+
name|prs
operator|+
literal|" of "
operator|+
name|oname
operator|+
literal|" threw an exception"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InstanceNotFoundException
name|e
parameter_list|)
block|{
comment|//Ignored for some reason the bean was not found so don't output it
continue|continue;
block|}
catch|catch
parameter_list|(
name|IntrospectionException
name|e
parameter_list|)
block|{
comment|// This is an internal error, something odd happened with reflection so
comment|// log it and don't output the bean.
name|LOG
operator|.
name|error
argument_list|(
literal|"Problem while trying to process JMX query: "
operator|+
name|qry
operator|+
literal|" with MBean "
operator|+
name|oname
argument_list|,
name|e
argument_list|)
expr_stmt|;
continue|continue;
block|}
catch|catch
parameter_list|(
name|ReflectionException
name|e
parameter_list|)
block|{
comment|// This happens when the code inside the JMX bean threw an exception, so
comment|// log it and don't output the bean.
name|LOG
operator|.
name|error
argument_list|(
literal|"Problem while trying to process JMX query: "
operator|+
name|qry
operator|+
literal|" with MBean "
operator|+
name|oname
argument_list|,
name|e
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|jg
operator|.
name|writeStartObject
argument_list|()
expr_stmt|;
name|jg
operator|.
name|writeStringField
argument_list|(
literal|"name"
argument_list|,
name|oname
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|jg
operator|.
name|writeStringField
argument_list|(
literal|"modelerType"
argument_list|,
name|code
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|attribute
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|attributeinfo
operator|==
literal|null
operator|)
condition|)
block|{
name|jg
operator|.
name|writeStringField
argument_list|(
literal|"result"
argument_list|,
literal|"ERROR"
argument_list|)
expr_stmt|;
name|jg
operator|.
name|writeStringField
argument_list|(
literal|"message"
argument_list|,
literal|"No attribute with name "
operator|+
name|attribute
operator|+
literal|" was found."
argument_list|)
expr_stmt|;
name|jg
operator|.
name|writeEndObject
argument_list|()
expr_stmt|;
name|jg
operator|.
name|writeEndArray
argument_list|()
expr_stmt|;
name|jg
operator|.
name|close
argument_list|()
expr_stmt|;
name|response
operator|.
name|setStatus
argument_list|(
name|HttpServletResponse
operator|.
name|SC_NOT_FOUND
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|attribute
operator|!=
literal|null
condition|)
block|{
name|writeAttribute
argument_list|(
name|jg
argument_list|,
name|attribute
argument_list|,
name|attributeinfo
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|MBeanAttributeInfo
name|attrs
index|[]
init|=
name|minfo
operator|.
name|getAttributes
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|attrs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|writeAttribute
argument_list|(
name|jg
argument_list|,
name|oname
argument_list|,
name|attrs
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
name|jg
operator|.
name|writeEndObject
argument_list|()
expr_stmt|;
block|}
name|jg
operator|.
name|writeEndArray
argument_list|()
expr_stmt|;
block|}
DECL|method|writeAttribute (JsonGenerator jg, ObjectName oname, MBeanAttributeInfo attr)
specifier|private
name|void
name|writeAttribute
parameter_list|(
name|JsonGenerator
name|jg
parameter_list|,
name|ObjectName
name|oname
parameter_list|,
name|MBeanAttributeInfo
name|attr
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|attr
operator|.
name|isReadable
argument_list|()
condition|)
block|{
return|return;
block|}
name|String
name|attName
init|=
name|attr
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"modelerType"
operator|.
name|equals
argument_list|(
name|attName
argument_list|)
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|attName
operator|.
name|indexOf
argument_list|(
literal|"="
argument_list|)
operator|>=
literal|0
operator|||
name|attName
operator|.
name|indexOf
argument_list|(
literal|":"
argument_list|)
operator|>=
literal|0
operator|||
name|attName
operator|.
name|indexOf
argument_list|(
literal|" "
argument_list|)
operator|>=
literal|0
condition|)
block|{
return|return;
block|}
name|Object
name|value
init|=
literal|null
decl_stmt|;
try|try
block|{
name|value
operator|=
name|mBeanServer
operator|.
name|getAttribute
argument_list|(
name|oname
argument_list|,
name|attName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeMBeanException
name|e
parameter_list|)
block|{
comment|// UnsupportedOperationExceptions happen in the normal course of business,
comment|// so no need to log them as errors all the time.
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|UnsupportedOperationException
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"getting attribute "
operator|+
name|attName
operator|+
literal|" of "
operator|+
name|oname
operator|+
literal|" threw an exception"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"getting attribute "
operator|+
name|attName
operator|+
literal|" of "
operator|+
name|oname
operator|+
literal|" threw an exception"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
catch|catch
parameter_list|(
name|RuntimeErrorException
name|e
parameter_list|)
block|{
comment|// RuntimeErrorException happens when an unexpected failure occurs in getAttribute
comment|// for example https://issues.apache.org/jira/browse/DAEMON-120
name|LOG
operator|.
name|debug
argument_list|(
literal|"getting attribute "
operator|+
name|attName
operator|+
literal|" of "
operator|+
name|oname
operator|+
literal|" threw an exception"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
catch|catch
parameter_list|(
name|AttributeNotFoundException
name|e
parameter_list|)
block|{
comment|//Ignored the attribute was not found, which should never happen because the bean
comment|//just told us that it has this attribute, but if this happens just don't output
comment|//the attribute.
return|return;
block|}
catch|catch
parameter_list|(
name|MBeanException
name|e
parameter_list|)
block|{
comment|//The code inside the attribute getter threw an exception so log it, and
comment|// skip outputting the attribute
name|LOG
operator|.
name|error
argument_list|(
literal|"getting attribute "
operator|+
name|attName
operator|+
literal|" of "
operator|+
name|oname
operator|+
literal|" threw an exception"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
comment|//For some reason even with an MBeanException available to them Runtime exceptions
comment|//can still find their way through, so treat them the same as MBeanException
name|LOG
operator|.
name|error
argument_list|(
literal|"getting attribute "
operator|+
name|attName
operator|+
literal|" of "
operator|+
name|oname
operator|+
literal|" threw an exception"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
catch|catch
parameter_list|(
name|ReflectionException
name|e
parameter_list|)
block|{
comment|//This happens when the code inside the JMX bean (setter?? from the java docs)
comment|//threw an exception, so log it and skip outputting the attribute
name|LOG
operator|.
name|error
argument_list|(
literal|"getting attribute "
operator|+
name|attName
operator|+
literal|" of "
operator|+
name|oname
operator|+
literal|" threw an exception"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
catch|catch
parameter_list|(
name|InstanceNotFoundException
name|e
parameter_list|)
block|{
comment|//Ignored the mbean itself was not found, which should never happen because we
comment|//just accessed it (perhaps something unregistered in-between) but if this
comment|//happens just don't output the attribute.
return|return;
block|}
name|writeAttribute
argument_list|(
name|jg
argument_list|,
name|attName
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|writeAttribute (JsonGenerator jg, String attName, Object value)
specifier|private
name|void
name|writeAttribute
parameter_list|(
name|JsonGenerator
name|jg
parameter_list|,
name|String
name|attName
parameter_list|,
name|Object
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|jg
operator|.
name|writeFieldName
argument_list|(
name|attName
argument_list|)
expr_stmt|;
name|writeObject
argument_list|(
name|jg
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|writeObject (JsonGenerator jg, Object value)
specifier|private
name|void
name|writeObject
parameter_list|(
name|JsonGenerator
name|jg
parameter_list|,
name|Object
name|value
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|jg
operator|.
name|writeNull
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|c
init|=
name|value
operator|.
name|getClass
argument_list|()
decl_stmt|;
if|if
condition|(
name|c
operator|.
name|isArray
argument_list|()
condition|)
block|{
name|jg
operator|.
name|writeStartArray
argument_list|()
expr_stmt|;
name|int
name|len
init|=
name|Array
operator|.
name|getLength
argument_list|(
name|value
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|len
condition|;
name|j
operator|++
control|)
block|{
name|Object
name|item
init|=
name|Array
operator|.
name|get
argument_list|(
name|value
argument_list|,
name|j
argument_list|)
decl_stmt|;
name|writeObject
argument_list|(
name|jg
argument_list|,
name|item
argument_list|)
expr_stmt|;
block|}
name|jg
operator|.
name|writeEndArray
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|instanceof
name|Number
condition|)
block|{
name|Number
name|n
init|=
operator|(
name|Number
operator|)
name|value
decl_stmt|;
name|jg
operator|.
name|writeNumber
argument_list|(
name|n
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|instanceof
name|Boolean
condition|)
block|{
name|Boolean
name|b
init|=
operator|(
name|Boolean
operator|)
name|value
decl_stmt|;
name|jg
operator|.
name|writeBoolean
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|instanceof
name|CompositeData
condition|)
block|{
name|CompositeData
name|cds
init|=
operator|(
name|CompositeData
operator|)
name|value
decl_stmt|;
name|CompositeType
name|comp
init|=
name|cds
operator|.
name|getCompositeType
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|keys
init|=
name|comp
operator|.
name|keySet
argument_list|()
decl_stmt|;
name|jg
operator|.
name|writeStartObject
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|key
range|:
name|keys
control|)
block|{
name|writeAttribute
argument_list|(
name|jg
argument_list|,
name|key
argument_list|,
name|cds
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|jg
operator|.
name|writeEndObject
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|instanceof
name|TabularData
condition|)
block|{
name|TabularData
name|tds
init|=
operator|(
name|TabularData
operator|)
name|value
decl_stmt|;
name|jg
operator|.
name|writeStartArray
argument_list|()
expr_stmt|;
for|for
control|(
name|Object
name|entry
range|:
name|tds
operator|.
name|values
argument_list|()
control|)
block|{
name|writeObject
argument_list|(
name|jg
argument_list|,
name|entry
argument_list|)
expr_stmt|;
block|}
name|jg
operator|.
name|writeEndArray
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|jg
operator|.
name|writeString
argument_list|(
name|value
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

