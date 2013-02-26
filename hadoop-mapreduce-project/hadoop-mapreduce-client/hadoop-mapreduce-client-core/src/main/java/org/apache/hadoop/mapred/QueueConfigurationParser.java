begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p/>  * http://www.apache.org/licenses/LICENSE-2.0  *<p/>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
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
name|io
operator|.
name|IOUtils
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
name|mapreduce
operator|.
name|MRConfig
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
name|mapreduce
operator|.
name|QueueState
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
name|authorize
operator|.
name|AccessControlList
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|QueueManager
operator|.
name|toFullPropertyName
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|NamedNodeMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|NodeList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|DOMException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|ParserConfigurationException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilderFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilder
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
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_comment
comment|/**  * Class for parsing mapred-queues.xml.  *    The format consists nesting of  *    queues within queues - a feature called hierarchical queues.  *    The parser expects that queues are  *    defined within the 'queues' tag which is the top level element for  *    XML document.  *   * Creates the complete queue hieararchy  */
end_comment

begin_class
DECL|class|QueueConfigurationParser
class|class
name|QueueConfigurationParser
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
name|QueueConfigurationParser
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|aclsEnabled
specifier|private
name|boolean
name|aclsEnabled
init|=
literal|false
decl_stmt|;
comment|//Default root.
DECL|field|root
specifier|protected
name|Queue
name|root
init|=
literal|null
decl_stmt|;
comment|//xml tags for mapred-queues.xml
DECL|field|NAME_SEPARATOR
specifier|static
specifier|final
name|String
name|NAME_SEPARATOR
init|=
literal|":"
decl_stmt|;
DECL|field|QUEUE_TAG
specifier|static
specifier|final
name|String
name|QUEUE_TAG
init|=
literal|"queue"
decl_stmt|;
DECL|field|ACL_SUBMIT_JOB_TAG
specifier|static
specifier|final
name|String
name|ACL_SUBMIT_JOB_TAG
init|=
literal|"acl-submit-job"
decl_stmt|;
DECL|field|ACL_ADMINISTER_JOB_TAG
specifier|static
specifier|final
name|String
name|ACL_ADMINISTER_JOB_TAG
init|=
literal|"acl-administer-jobs"
decl_stmt|;
comment|// The value read from queues config file for this tag is not used at all.
comment|// To enable queue acls and job acls, mapreduce.cluster.acls.enabled is
comment|// to be set in mapred-site.xml
annotation|@
name|Deprecated
DECL|field|ACLS_ENABLED_TAG
specifier|static
specifier|final
name|String
name|ACLS_ENABLED_TAG
init|=
literal|"aclsEnabled"
decl_stmt|;
DECL|field|PROPERTIES_TAG
specifier|static
specifier|final
name|String
name|PROPERTIES_TAG
init|=
literal|"properties"
decl_stmt|;
DECL|field|STATE_TAG
specifier|static
specifier|final
name|String
name|STATE_TAG
init|=
literal|"state"
decl_stmt|;
DECL|field|QUEUE_NAME_TAG
specifier|static
specifier|final
name|String
name|QUEUE_NAME_TAG
init|=
literal|"name"
decl_stmt|;
DECL|field|QUEUES_TAG
specifier|static
specifier|final
name|String
name|QUEUES_TAG
init|=
literal|"queues"
decl_stmt|;
DECL|field|PROPERTY_TAG
specifier|static
specifier|final
name|String
name|PROPERTY_TAG
init|=
literal|"property"
decl_stmt|;
DECL|field|KEY_TAG
specifier|static
specifier|final
name|String
name|KEY_TAG
init|=
literal|"key"
decl_stmt|;
DECL|field|VALUE_TAG
specifier|static
specifier|final
name|String
name|VALUE_TAG
init|=
literal|"value"
decl_stmt|;
comment|/**    * Default constructor for DeperacatedQueueConfigurationParser    */
DECL|method|QueueConfigurationParser ()
name|QueueConfigurationParser
parameter_list|()
block|{        }
DECL|method|QueueConfigurationParser (String confFile, boolean areAclsEnabled)
name|QueueConfigurationParser
parameter_list|(
name|String
name|confFile
parameter_list|,
name|boolean
name|areAclsEnabled
parameter_list|)
block|{
name|aclsEnabled
operator|=
name|areAclsEnabled
expr_stmt|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|confFile
argument_list|)
operator|.
name|getAbsoluteFile
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|file
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Configuration file not found at "
operator|+
name|confFile
argument_list|)
throw|;
block|}
name|InputStream
name|in
init|=
literal|null
decl_stmt|;
try|try
block|{
name|in
operator|=
operator|new
name|BufferedInputStream
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
argument_list|)
expr_stmt|;
name|loadFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ioe
argument_list|)
throw|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|QueueConfigurationParser (InputStream xmlInput, boolean areAclsEnabled)
name|QueueConfigurationParser
parameter_list|(
name|InputStream
name|xmlInput
parameter_list|,
name|boolean
name|areAclsEnabled
parameter_list|)
block|{
name|aclsEnabled
operator|=
name|areAclsEnabled
expr_stmt|;
name|loadFrom
argument_list|(
name|xmlInput
argument_list|)
expr_stmt|;
block|}
DECL|method|loadFrom (InputStream xmlInput)
specifier|private
name|void
name|loadFrom
parameter_list|(
name|InputStream
name|xmlInput
parameter_list|)
block|{
try|try
block|{
name|this
operator|.
name|root
operator|=
name|loadResource
argument_list|(
name|xmlInput
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParserConfigurationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|setAclsEnabled (boolean aclsEnabled)
name|void
name|setAclsEnabled
parameter_list|(
name|boolean
name|aclsEnabled
parameter_list|)
block|{
name|this
operator|.
name|aclsEnabled
operator|=
name|aclsEnabled
expr_stmt|;
block|}
DECL|method|isAclsEnabled ()
name|boolean
name|isAclsEnabled
parameter_list|()
block|{
return|return
name|aclsEnabled
return|;
block|}
DECL|method|getRoot ()
name|Queue
name|getRoot
parameter_list|()
block|{
return|return
name|root
return|;
block|}
DECL|method|setRoot (Queue root)
name|void
name|setRoot
parameter_list|(
name|Queue
name|root
parameter_list|)
block|{
name|this
operator|.
name|root
operator|=
name|root
expr_stmt|;
block|}
comment|/**    * Method to load the resource file.    * generates the root.    *     * @param resourceInput InputStream that provides the XML to parse    * @return    * @throws ParserConfigurationException    * @throws SAXException    * @throws IOException    */
DECL|method|loadResource (InputStream resourceInput)
specifier|protected
name|Queue
name|loadResource
parameter_list|(
name|InputStream
name|resourceInput
parameter_list|)
throws|throws
name|ParserConfigurationException
throws|,
name|SAXException
throws|,
name|IOException
block|{
name|DocumentBuilderFactory
name|docBuilderFactory
init|=
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
comment|//ignore all comments inside the xml file
name|docBuilderFactory
operator|.
name|setIgnoringComments
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|//allow includes in the xml file
name|docBuilderFactory
operator|.
name|setNamespaceAware
argument_list|(
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
name|docBuilderFactory
operator|.
name|setXIncludeAware
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Failed to set setXIncludeAware(true) for parser "
operator|+
name|docBuilderFactory
operator|+
name|NAME_SEPARATOR
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
name|DocumentBuilder
name|builder
init|=
name|docBuilderFactory
operator|.
name|newDocumentBuilder
argument_list|()
decl_stmt|;
name|Document
name|doc
init|=
literal|null
decl_stmt|;
name|Element
name|queuesNode
init|=
literal|null
decl_stmt|;
name|doc
operator|=
name|builder
operator|.
name|parse
argument_list|(
name|resourceInput
argument_list|)
expr_stmt|;
name|queuesNode
operator|=
name|doc
operator|.
name|getDocumentElement
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|parseResource
argument_list|(
name|queuesNode
argument_list|)
return|;
block|}
DECL|method|parseResource (Element queuesNode)
specifier|private
name|Queue
name|parseResource
parameter_list|(
name|Element
name|queuesNode
parameter_list|)
block|{
name|Queue
name|rootNode
init|=
literal|null
decl_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|QUEUES_TAG
operator|.
name|equals
argument_list|(
name|queuesNode
operator|.
name|getTagName
argument_list|()
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Bad conf file: top-level element not<queues>"
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"No queues defined "
argument_list|)
throw|;
block|}
name|NamedNodeMap
name|nmp
init|=
name|queuesNode
operator|.
name|getAttributes
argument_list|()
decl_stmt|;
name|Node
name|acls
init|=
name|nmp
operator|.
name|getNamedItem
argument_list|(
name|ACLS_ENABLED_TAG
argument_list|)
decl_stmt|;
if|if
condition|(
name|acls
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Configuring "
operator|+
name|ACLS_ENABLED_TAG
operator|+
literal|" flag in "
operator|+
name|QueueManager
operator|.
name|QUEUE_CONF_FILE_NAME
operator|+
literal|" is not valid. "
operator|+
literal|"This tag is ignored. Configure "
operator|+
name|MRConfig
operator|.
name|MR_ACLS_ENABLED
operator|+
literal|" in mapred-site.xml. See the "
operator|+
literal|" documentation of "
operator|+
name|MRConfig
operator|.
name|MR_ACLS_ENABLED
operator|+
literal|", which is used for enabling job level authorization and "
operator|+
literal|" queue level authorization."
argument_list|)
expr_stmt|;
block|}
name|NodeList
name|props
init|=
name|queuesNode
operator|.
name|getChildNodes
argument_list|()
decl_stmt|;
if|if
condition|(
name|props
operator|==
literal|null
operator|||
name|props
operator|.
name|getLength
argument_list|()
operator|<=
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|" Bad configuration no queues defined "
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|" No queues defined "
argument_list|)
throw|;
block|}
comment|//We have root level nodes.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|props
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Node
name|propNode
init|=
name|props
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|propNode
operator|instanceof
name|Element
operator|)
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
operator|!
name|propNode
operator|.
name|getNodeName
argument_list|()
operator|.
name|equals
argument_list|(
name|QUEUE_TAG
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"At root level only \" queue \" tags are allowed "
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Malformed xml document no queue defined "
argument_list|)
throw|;
block|}
name|Element
name|prop
init|=
operator|(
name|Element
operator|)
name|propNode
decl_stmt|;
comment|//Add children to root.
name|Queue
name|q
init|=
name|createHierarchy
argument_list|(
literal|""
argument_list|,
name|prop
argument_list|)
decl_stmt|;
if|if
condition|(
name|rootNode
operator|==
literal|null
condition|)
block|{
name|rootNode
operator|=
operator|new
name|Queue
argument_list|()
expr_stmt|;
name|rootNode
operator|.
name|setName
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
name|rootNode
operator|.
name|addChild
argument_list|(
name|q
argument_list|)
expr_stmt|;
block|}
return|return
name|rootNode
return|;
block|}
catch|catch
parameter_list|(
name|DOMException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Error parsing conf file: "
operator|+
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * @param parent Name of the parent queue    * @param queueNode    * @return    */
DECL|method|createHierarchy (String parent, Element queueNode)
specifier|private
name|Queue
name|createHierarchy
parameter_list|(
name|String
name|parent
parameter_list|,
name|Element
name|queueNode
parameter_list|)
block|{
if|if
condition|(
name|queueNode
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|//Name of the current queue.
comment|//Complete qualified queue name.
name|String
name|name
init|=
literal|""
decl_stmt|;
name|Queue
name|newQueue
init|=
operator|new
name|Queue
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|AccessControlList
argument_list|>
name|acls
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|AccessControlList
argument_list|>
argument_list|()
decl_stmt|;
name|NodeList
name|fields
init|=
name|queueNode
operator|.
name|getChildNodes
argument_list|()
decl_stmt|;
name|validate
argument_list|(
name|queueNode
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Element
argument_list|>
name|subQueues
init|=
operator|new
name|ArrayList
argument_list|<
name|Element
argument_list|>
argument_list|()
decl_stmt|;
name|String
name|submitKey
init|=
literal|""
decl_stmt|;
name|String
name|adminKey
init|=
literal|""
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
name|fields
operator|.
name|getLength
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|Node
name|fieldNode
init|=
name|fields
operator|.
name|item
argument_list|(
name|j
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|fieldNode
operator|instanceof
name|Element
operator|)
condition|)
block|{
continue|continue;
block|}
name|Element
name|field
init|=
operator|(
name|Element
operator|)
name|fieldNode
decl_stmt|;
if|if
condition|(
name|QUEUE_NAME_TAG
operator|.
name|equals
argument_list|(
name|field
operator|.
name|getTagName
argument_list|()
argument_list|)
condition|)
block|{
name|String
name|nameValue
init|=
name|field
operator|.
name|getTextContent
argument_list|()
decl_stmt|;
if|if
condition|(
name|field
operator|.
name|getTextContent
argument_list|()
operator|==
literal|null
operator|||
name|field
operator|.
name|getTextContent
argument_list|()
operator|.
name|trim
argument_list|()
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
operator|||
name|field
operator|.
name|getTextContent
argument_list|()
operator|.
name|contains
argument_list|(
name|NAME_SEPARATOR
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Improper queue name : "
operator|+
name|nameValue
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|parent
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
block|{
name|name
operator|+=
name|parent
operator|+
name|NAME_SEPARATOR
expr_stmt|;
block|}
comment|//generate the complete qualified name
comment|//parent.child
name|name
operator|+=
name|nameValue
expr_stmt|;
name|newQueue
operator|.
name|setName
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|submitKey
operator|=
name|toFullPropertyName
argument_list|(
name|name
argument_list|,
name|QueueACL
operator|.
name|SUBMIT_JOB
operator|.
name|getAclName
argument_list|()
argument_list|)
expr_stmt|;
name|adminKey
operator|=
name|toFullPropertyName
argument_list|(
name|name
argument_list|,
name|QueueACL
operator|.
name|ADMINISTER_JOBS
operator|.
name|getAclName
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|QUEUE_TAG
operator|.
name|equals
argument_list|(
name|field
operator|.
name|getTagName
argument_list|()
argument_list|)
operator|&&
name|field
operator|.
name|hasChildNodes
argument_list|()
condition|)
block|{
name|subQueues
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|isAclsEnabled
argument_list|()
condition|)
block|{
if|if
condition|(
name|ACL_SUBMIT_JOB_TAG
operator|.
name|equals
argument_list|(
name|field
operator|.
name|getTagName
argument_list|()
argument_list|)
condition|)
block|{
name|acls
operator|.
name|put
argument_list|(
name|submitKey
argument_list|,
operator|new
name|AccessControlList
argument_list|(
name|field
operator|.
name|getTextContent
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|ACL_ADMINISTER_JOB_TAG
operator|.
name|equals
argument_list|(
name|field
operator|.
name|getTagName
argument_list|()
argument_list|)
condition|)
block|{
name|acls
operator|.
name|put
argument_list|(
name|adminKey
argument_list|,
operator|new
name|AccessControlList
argument_list|(
name|field
operator|.
name|getTextContent
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|PROPERTIES_TAG
operator|.
name|equals
argument_list|(
name|field
operator|.
name|getTagName
argument_list|()
argument_list|)
condition|)
block|{
name|Properties
name|properties
init|=
name|populateProperties
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|newQueue
operator|.
name|setProperties
argument_list|(
name|properties
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|STATE_TAG
operator|.
name|equals
argument_list|(
name|field
operator|.
name|getTagName
argument_list|()
argument_list|)
condition|)
block|{
name|String
name|state
init|=
name|field
operator|.
name|getTextContent
argument_list|()
decl_stmt|;
name|newQueue
operator|.
name|setState
argument_list|(
name|QueueState
operator|.
name|getState
argument_list|(
name|state
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|acls
operator|.
name|containsKey
argument_list|(
name|submitKey
argument_list|)
condition|)
block|{
name|acls
operator|.
name|put
argument_list|(
name|submitKey
argument_list|,
operator|new
name|AccessControlList
argument_list|(
literal|" "
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|acls
operator|.
name|containsKey
argument_list|(
name|adminKey
argument_list|)
condition|)
block|{
name|acls
operator|.
name|put
argument_list|(
name|adminKey
argument_list|,
operator|new
name|AccessControlList
argument_list|(
literal|" "
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//Set acls
name|newQueue
operator|.
name|setAcls
argument_list|(
name|acls
argument_list|)
expr_stmt|;
comment|//At this point we have the queue ready at current height level.
comment|//so we have parent name available.
for|for
control|(
name|Element
name|field
range|:
name|subQueues
control|)
block|{
name|newQueue
operator|.
name|addChild
argument_list|(
name|createHierarchy
argument_list|(
name|newQueue
operator|.
name|getName
argument_list|()
argument_list|,
name|field
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|newQueue
return|;
block|}
comment|/**    * Populate the properties for Queue    *    * @param field    * @return    */
DECL|method|populateProperties (Element field)
specifier|private
name|Properties
name|populateProperties
parameter_list|(
name|Element
name|field
parameter_list|)
block|{
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|NodeList
name|propfields
init|=
name|field
operator|.
name|getChildNodes
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
name|propfields
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Node
name|prop
init|=
name|propfields
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
comment|//If this node is not of type element
comment|//skip this.
if|if
condition|(
operator|!
operator|(
name|prop
operator|instanceof
name|Element
operator|)
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|PROPERTY_TAG
operator|.
name|equals
argument_list|(
name|prop
operator|.
name|getNodeName
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|prop
operator|.
name|hasAttributes
argument_list|()
condition|)
block|{
name|NamedNodeMap
name|nmp
init|=
name|prop
operator|.
name|getAttributes
argument_list|()
decl_stmt|;
if|if
condition|(
name|nmp
operator|.
name|getNamedItem
argument_list|(
name|KEY_TAG
argument_list|)
operator|!=
literal|null
operator|&&
name|nmp
operator|.
name|getNamedItem
argument_list|(
name|VALUE_TAG
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|props
operator|.
name|setProperty
argument_list|(
name|nmp
operator|.
name|getNamedItem
argument_list|(
name|KEY_TAG
argument_list|)
operator|.
name|getTextContent
argument_list|()
argument_list|,
name|nmp
operator|.
name|getNamedItem
argument_list|(
name|VALUE_TAG
argument_list|)
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|props
return|;
block|}
comment|/**    *    * Checks if there is NAME_TAG for queues.    *    * Checks if (queue has children)    *  then it shouldnot have acls-* or state    *   else    *  throws an Exception.    * @param node    */
DECL|method|validate (Node node)
specifier|private
name|void
name|validate
parameter_list|(
name|Node
name|node
parameter_list|)
block|{
name|NodeList
name|fields
init|=
name|node
operator|.
name|getChildNodes
argument_list|()
decl_stmt|;
comment|//Check if<queue>& (<acls-*> ||<state>) are not siblings
comment|//if yes throw an IOException.
name|Set
argument_list|<
name|String
argument_list|>
name|siblings
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
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
name|fields
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
operator|(
name|fields
operator|.
name|item
argument_list|(
name|i
argument_list|)
operator|instanceof
name|Element
operator|)
condition|)
block|{
continue|continue;
block|}
name|siblings
operator|.
name|add
argument_list|(
operator|(
name|fields
operator|.
name|item
argument_list|(
name|i
argument_list|)
operator|)
operator|.
name|getNodeName
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|siblings
operator|.
name|contains
argument_list|(
name|QUEUE_NAME_TAG
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|" Malformed xml formation queue name not specified "
argument_list|)
throw|;
block|}
if|if
condition|(
name|siblings
operator|.
name|contains
argument_list|(
name|QUEUE_TAG
argument_list|)
operator|&&
operator|(
name|siblings
operator|.
name|contains
argument_list|(
name|ACL_ADMINISTER_JOB_TAG
argument_list|)
operator|||
name|siblings
operator|.
name|contains
argument_list|(
name|ACL_SUBMIT_JOB_TAG
argument_list|)
operator|||
name|siblings
operator|.
name|contains
argument_list|(
name|STATE_TAG
argument_list|)
operator|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|" Malformed xml formation queue tag and acls "
operator|+
literal|"tags or state tags are siblings "
argument_list|)
throw|;
block|}
block|}
DECL|method|getSimpleQueueName (String fullQName)
specifier|private
specifier|static
name|String
name|getSimpleQueueName
parameter_list|(
name|String
name|fullQName
parameter_list|)
block|{
name|int
name|index
init|=
name|fullQName
operator|.
name|lastIndexOf
argument_list|(
name|NAME_SEPARATOR
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|<
literal|0
condition|)
block|{
return|return
name|fullQName
return|;
block|}
return|return
name|fullQName
operator|.
name|substring
argument_list|(
name|index
operator|+
literal|1
argument_list|,
name|fullQName
operator|.
name|length
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Construct an {@link Element} for a single queue, constructing the inner    * queue&lt;name/&gt;,&lt;properties/&gt;,&lt;state/&gt; and the inner    *&lt;queue&gt; elements recursively.    *     * @param document    * @param jqi    * @return    */
DECL|method|getQueueElement (Document document, JobQueueInfo jqi)
specifier|static
name|Element
name|getQueueElement
parameter_list|(
name|Document
name|document
parameter_list|,
name|JobQueueInfo
name|jqi
parameter_list|)
block|{
comment|// Queue
name|Element
name|q
init|=
name|document
operator|.
name|createElement
argument_list|(
name|QUEUE_TAG
argument_list|)
decl_stmt|;
comment|// Queue-name
name|Element
name|qName
init|=
name|document
operator|.
name|createElement
argument_list|(
name|QUEUE_NAME_TAG
argument_list|)
decl_stmt|;
name|qName
operator|.
name|setTextContent
argument_list|(
name|getSimpleQueueName
argument_list|(
name|jqi
operator|.
name|getQueueName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|.
name|appendChild
argument_list|(
name|qName
argument_list|)
expr_stmt|;
comment|// Queue-properties
name|Properties
name|props
init|=
name|jqi
operator|.
name|getProperties
argument_list|()
decl_stmt|;
name|Element
name|propsElement
init|=
name|document
operator|.
name|createElement
argument_list|(
name|PROPERTIES_TAG
argument_list|)
decl_stmt|;
if|if
condition|(
name|props
operator|!=
literal|null
condition|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|propList
init|=
name|props
operator|.
name|stringPropertyNames
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|prop
range|:
name|propList
control|)
block|{
name|Element
name|propertyElement
init|=
name|document
operator|.
name|createElement
argument_list|(
name|PROPERTY_TAG
argument_list|)
decl_stmt|;
name|propertyElement
operator|.
name|setAttribute
argument_list|(
name|KEY_TAG
argument_list|,
name|prop
argument_list|)
expr_stmt|;
name|propertyElement
operator|.
name|setAttribute
argument_list|(
name|VALUE_TAG
argument_list|,
operator|(
name|String
operator|)
name|props
operator|.
name|get
argument_list|(
name|prop
argument_list|)
argument_list|)
expr_stmt|;
name|propsElement
operator|.
name|appendChild
argument_list|(
name|propertyElement
argument_list|)
expr_stmt|;
block|}
block|}
name|q
operator|.
name|appendChild
argument_list|(
name|propsElement
argument_list|)
expr_stmt|;
comment|// Queue-state
name|String
name|queueState
init|=
name|jqi
operator|.
name|getState
argument_list|()
operator|.
name|getStateName
argument_list|()
decl_stmt|;
if|if
condition|(
name|queueState
operator|!=
literal|null
operator|&&
operator|!
name|queueState
operator|.
name|equals
argument_list|(
name|QueueState
operator|.
name|UNDEFINED
operator|.
name|getStateName
argument_list|()
argument_list|)
condition|)
block|{
name|Element
name|qStateElement
init|=
name|document
operator|.
name|createElement
argument_list|(
name|STATE_TAG
argument_list|)
decl_stmt|;
name|qStateElement
operator|.
name|setTextContent
argument_list|(
name|queueState
argument_list|)
expr_stmt|;
name|q
operator|.
name|appendChild
argument_list|(
name|qStateElement
argument_list|)
expr_stmt|;
block|}
comment|// Queue-children
name|List
argument_list|<
name|JobQueueInfo
argument_list|>
name|children
init|=
name|jqi
operator|.
name|getChildren
argument_list|()
decl_stmt|;
if|if
condition|(
name|children
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|JobQueueInfo
name|child
range|:
name|children
control|)
block|{
name|q
operator|.
name|appendChild
argument_list|(
name|getQueueElement
argument_list|(
name|document
argument_list|,
name|child
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|q
return|;
block|}
block|}
end_class

end_unit

