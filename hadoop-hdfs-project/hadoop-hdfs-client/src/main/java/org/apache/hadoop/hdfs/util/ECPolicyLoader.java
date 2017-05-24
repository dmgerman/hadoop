begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|util
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
name|io
operator|.
name|erasurecode
operator|.
name|ECSchema
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
name|hdfs
operator|.
name|protocol
operator|.
name|ErasureCodingPolicy
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
name|Text
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
name|NodeList
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
name|ParserConfigurationException
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|MalformedURLException
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
name|List
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
comment|/**  * A EC policy loading tool that loads user defined EC policies from XML file.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|ECPolicyLoader
specifier|public
class|class
name|ECPolicyLoader
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
name|ECPolicyLoader
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|LAYOUT_VERSION
specifier|private
specifier|static
specifier|final
name|int
name|LAYOUT_VERSION
init|=
literal|1
decl_stmt|;
comment|/**    * Load user defined EC policies from a XML configuration file.    * @param policyFilePath path of EC policy file    * @return all valid EC policies in EC policy file    */
DECL|method|loadPolicy (String policyFilePath)
specifier|public
name|List
argument_list|<
name|ErasureCodingPolicy
argument_list|>
name|loadPolicy
parameter_list|(
name|String
name|policyFilePath
parameter_list|)
block|{
try|try
block|{
name|File
name|policyFile
init|=
name|getPolicyFile
argument_list|(
name|policyFilePath
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|policyFile
operator|.
name|exists
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Not found any EC policy file"
argument_list|)
expr_stmt|;
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
return|return
name|loadECPolicies
argument_list|(
name|policyFile
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ParserConfigurationException
decl||
name|IOException
decl||
name|SAXException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Failed to load EC policy file: "
operator|+
name|policyFilePath
argument_list|)
throw|;
block|}
block|}
comment|/**    * Load EC policies from a XML configuration file.    * @param policyFile EC policy file    * @return list of EC policies    * @throws ParserConfigurationException if ParserConfigurationException happen    * @throws IOException if no such EC policy file    * @throws SAXException if the xml file has some invalid elements    */
DECL|method|loadECPolicies (File policyFile)
specifier|private
name|List
argument_list|<
name|ErasureCodingPolicy
argument_list|>
name|loadECPolicies
parameter_list|(
name|File
name|policyFile
parameter_list|)
throws|throws
name|ParserConfigurationException
throws|,
name|IOException
throws|,
name|SAXException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Loading EC policy file "
operator|+
name|policyFile
argument_list|)
expr_stmt|;
comment|// Read and parse the EC policy file.
name|DocumentBuilderFactory
name|dbf
init|=
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|dbf
operator|.
name|setIgnoringComments
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|DocumentBuilder
name|builder
init|=
name|dbf
operator|.
name|newDocumentBuilder
argument_list|()
decl_stmt|;
name|Document
name|doc
init|=
name|builder
operator|.
name|parse
argument_list|(
name|policyFile
argument_list|)
decl_stmt|;
name|Element
name|root
init|=
name|doc
operator|.
name|getDocumentElement
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
literal|"configuration"
operator|.
name|equals
argument_list|(
name|root
operator|.
name|getTagName
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Bad EC policy configuration file: "
operator|+
literal|"top-level element not<configuration>"
argument_list|)
throw|;
block|}
name|List
argument_list|<
name|ErasureCodingPolicy
argument_list|>
name|policies
decl_stmt|;
if|if
condition|(
name|root
operator|.
name|getElementsByTagName
argument_list|(
literal|"layoutversion"
argument_list|)
operator|.
name|getLength
argument_list|()
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|loadLayoutVersion
argument_list|(
name|root
argument_list|)
operator|==
name|LAYOUT_VERSION
condition|)
block|{
if|if
condition|(
name|root
operator|.
name|getElementsByTagName
argument_list|(
literal|"schemas"
argument_list|)
operator|.
name|getLength
argument_list|()
operator|>
literal|0
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|ECSchema
argument_list|>
name|schemas
init|=
name|loadSchemas
argument_list|(
name|root
argument_list|)
decl_stmt|;
if|if
condition|(
name|root
operator|.
name|getElementsByTagName
argument_list|(
literal|"policies"
argument_list|)
operator|.
name|getLength
argument_list|()
operator|>
literal|0
condition|)
block|{
name|policies
operator|=
name|loadPolicies
argument_list|(
name|root
argument_list|,
name|schemas
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Bad EC policy configuration file: "
operator|+
literal|"no<policies> element"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Bad EC policy configuration file: "
operator|+
literal|"no<schemas> element"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"The parse failed because of "
operator|+
literal|"bad layoutversion value"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Bad EC policy configuration file: "
operator|+
literal|"no<layoutVersion> element"
argument_list|)
throw|;
block|}
return|return
name|policies
return|;
block|}
comment|/**    * Load layoutVersion from root element in the XML configuration file.    * @param root root element    * @return layout version    */
DECL|method|loadLayoutVersion (Element root)
specifier|private
name|int
name|loadLayoutVersion
parameter_list|(
name|Element
name|root
parameter_list|)
block|{
name|int
name|layoutVersion
decl_stmt|;
name|Text
name|text
init|=
operator|(
name|Text
operator|)
name|root
operator|.
name|getElementsByTagName
argument_list|(
literal|"layoutversion"
argument_list|)
operator|.
name|item
argument_list|(
literal|0
argument_list|)
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
if|if
condition|(
name|text
operator|!=
literal|null
condition|)
block|{
name|String
name|value
init|=
name|text
operator|.
name|getData
argument_list|()
operator|.
name|trim
argument_list|()
decl_stmt|;
try|try
block|{
name|layoutVersion
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Bad layoutVersion value "
operator|+
name|value
operator|+
literal|" is found. It should be an integer"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Value of<layoutVersion> is null"
argument_list|)
throw|;
block|}
return|return
name|layoutVersion
return|;
block|}
comment|/**    * Load schemas from root element in the XML configuration file.    * @param root root element    * @return EC schema map    */
DECL|method|loadSchemas (Element root)
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|ECSchema
argument_list|>
name|loadSchemas
parameter_list|(
name|Element
name|root
parameter_list|)
block|{
name|NodeList
name|elements
init|=
name|root
operator|.
name|getElementsByTagName
argument_list|(
literal|"schemas"
argument_list|)
operator|.
name|item
argument_list|(
literal|0
argument_list|)
operator|.
name|getChildNodes
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|ECSchema
argument_list|>
name|schemas
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|ECSchema
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
name|elements
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Node
name|node
init|=
name|elements
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|instanceof
name|Element
condition|)
block|{
name|Element
name|element
init|=
operator|(
name|Element
operator|)
name|node
decl_stmt|;
if|if
condition|(
literal|"schema"
operator|.
name|equals
argument_list|(
name|element
operator|.
name|getTagName
argument_list|()
argument_list|)
condition|)
block|{
name|String
name|schemaId
init|=
name|element
operator|.
name|getAttribute
argument_list|(
literal|"id"
argument_list|)
decl_stmt|;
name|ECSchema
name|schema
init|=
name|loadSchema
argument_list|(
name|element
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|schemas
operator|.
name|containsValue
argument_list|(
name|schema
argument_list|)
condition|)
block|{
name|schemas
operator|.
name|put
argument_list|(
name|schemaId
argument_list|,
name|schema
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Repetitive schemas in EC policy"
operator|+
literal|" configuration file: "
operator|+
name|schemaId
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Bad element in EC policy"
operator|+
literal|" configuration file: "
operator|+
name|element
operator|.
name|getTagName
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
return|return
name|schemas
return|;
block|}
comment|/**    * Load EC policies from root element in the XML configuration file.    * @param root root element    * @param schemas schema map    * @return EC policy list    */
DECL|method|loadPolicies ( Element root, Map<String, ECSchema> schemas)
specifier|private
name|List
argument_list|<
name|ErasureCodingPolicy
argument_list|>
name|loadPolicies
parameter_list|(
name|Element
name|root
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|ECSchema
argument_list|>
name|schemas
parameter_list|)
block|{
name|NodeList
name|elements
init|=
name|root
operator|.
name|getElementsByTagName
argument_list|(
literal|"policies"
argument_list|)
operator|.
name|item
argument_list|(
literal|0
argument_list|)
operator|.
name|getChildNodes
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|ErasureCodingPolicy
argument_list|>
name|policies
init|=
operator|new
name|ArrayList
argument_list|<
name|ErasureCodingPolicy
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
name|elements
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Node
name|node
init|=
name|elements
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|instanceof
name|Element
condition|)
block|{
name|Element
name|element
init|=
operator|(
name|Element
operator|)
name|node
decl_stmt|;
if|if
condition|(
literal|"policy"
operator|.
name|equals
argument_list|(
name|element
operator|.
name|getTagName
argument_list|()
argument_list|)
condition|)
block|{
name|ErasureCodingPolicy
name|policy
init|=
name|loadPolicy
argument_list|(
name|element
argument_list|,
name|schemas
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|policies
operator|.
name|contains
argument_list|(
name|policy
argument_list|)
condition|)
block|{
name|policies
operator|.
name|add
argument_list|(
name|policy
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Repetitive policies in EC policy configuration file: "
operator|+
name|policy
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Bad element in EC policy configuration"
operator|+
literal|" file: "
operator|+
name|element
operator|.
name|getTagName
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
return|return
name|policies
return|;
block|}
comment|/**    * Path to the XML file containing user defined EC policies. If the path is    * relative, it is searched for in the classpath.    * @param policyFilePath path of EC policy file    * @return EC policy file    */
DECL|method|getPolicyFile (String policyFilePath)
specifier|private
name|File
name|getPolicyFile
parameter_list|(
name|String
name|policyFilePath
parameter_list|)
throws|throws
name|MalformedURLException
block|{
name|File
name|policyFile
init|=
operator|new
name|File
argument_list|(
name|policyFilePath
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|policyFile
operator|.
name|isAbsolute
argument_list|()
condition|)
block|{
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
name|policyFilePath
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|url
operator|.
name|getProtocol
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"file"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"EC policy file "
operator|+
name|url
operator|+
literal|" found on the classpath is not on the local filesystem."
argument_list|)
throw|;
block|}
else|else
block|{
name|policyFile
operator|=
operator|new
name|File
argument_list|(
name|url
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|policyFile
return|;
block|}
comment|/**    * Load a schema from a schema element in the XML configuration file.    * @param element EC schema element    * @return ECSchema    */
DECL|method|loadSchema (Element element)
specifier|private
name|ECSchema
name|loadSchema
parameter_list|(
name|Element
name|element
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|schemaOptions
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|NodeList
name|fields
init|=
name|element
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
name|fields
operator|.
name|getLength
argument_list|()
condition|;
name|i
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
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldNode
operator|instanceof
name|Element
condition|)
block|{
name|Element
name|field
init|=
operator|(
name|Element
operator|)
name|fieldNode
decl_stmt|;
name|String
name|tagName
init|=
name|field
operator|.
name|getTagName
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"k"
operator|.
name|equals
argument_list|(
name|tagName
argument_list|)
condition|)
block|{
name|tagName
operator|=
literal|"numDataUnits"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"m"
operator|.
name|equals
argument_list|(
name|tagName
argument_list|)
condition|)
block|{
name|tagName
operator|=
literal|"numParityUnits"
expr_stmt|;
block|}
comment|// Get the nonnull text value.
name|Text
name|text
init|=
operator|(
name|Text
operator|)
name|field
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
if|if
condition|(
name|text
operator|!=
literal|null
condition|)
block|{
name|String
name|value
init|=
name|text
operator|.
name|getData
argument_list|()
operator|.
name|trim
argument_list|()
decl_stmt|;
name|schemaOptions
operator|.
name|put
argument_list|(
name|tagName
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Value of<"
operator|+
name|tagName
operator|+
literal|"> is null"
argument_list|)
throw|;
block|}
block|}
block|}
return|return
operator|new
name|ECSchema
argument_list|(
name|schemaOptions
argument_list|)
return|;
block|}
comment|/**    * Load a EC policy from a policy element in the XML configuration file.    * @param element EC policy element    * @param schemas all valid schemas of the EC policy file    * @return EC policy    */
DECL|method|loadPolicy (Element element, Map<String, ECSchema> schemas)
specifier|private
name|ErasureCodingPolicy
name|loadPolicy
parameter_list|(
name|Element
name|element
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|ECSchema
argument_list|>
name|schemas
parameter_list|)
block|{
name|NodeList
name|fields
init|=
name|element
operator|.
name|getChildNodes
argument_list|()
decl_stmt|;
name|ECSchema
name|schema
init|=
literal|null
decl_stmt|;
name|int
name|cellSize
init|=
literal|0
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
name|Node
name|fieldNode
init|=
name|fields
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldNode
operator|instanceof
name|Element
condition|)
block|{
name|Element
name|field
init|=
operator|(
name|Element
operator|)
name|fieldNode
decl_stmt|;
name|String
name|tagName
init|=
name|field
operator|.
name|getTagName
argument_list|()
decl_stmt|;
comment|// Get the nonnull text value.
name|Text
name|text
init|=
operator|(
name|Text
operator|)
name|field
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
if|if
condition|(
name|text
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|text
operator|.
name|isElementContentWhitespace
argument_list|()
condition|)
block|{
name|String
name|value
init|=
name|text
operator|.
name|getData
argument_list|()
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"schema"
operator|.
name|equals
argument_list|(
name|tagName
argument_list|)
condition|)
block|{
name|schema
operator|=
name|schemas
operator|.
name|get
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"cellsize"
operator|.
name|equals
argument_list|(
name|tagName
argument_list|)
condition|)
block|{
try|try
block|{
name|cellSize
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Bad EC policy cellsize"
operator|+
literal|" value "
operator|+
name|value
operator|+
literal|" is found. It should be an integer"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Invalid tagName: "
operator|+
name|tagName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Value of<"
operator|+
name|tagName
operator|+
literal|"> is null"
argument_list|)
throw|;
block|}
block|}
block|}
if|if
condition|(
name|schema
operator|!=
literal|null
operator|&&
name|cellSize
operator|>
literal|0
condition|)
block|{
return|return
operator|new
name|ErasureCodingPolicy
argument_list|(
name|schema
argument_list|,
name|cellSize
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Bad policy is found in"
operator|+
literal|" EC policy configuration file"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

