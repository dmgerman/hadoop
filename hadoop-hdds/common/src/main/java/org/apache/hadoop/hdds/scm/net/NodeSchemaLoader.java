begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.net
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|scm
operator|.
name|net
package|;
end_package

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
name|Text
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
name|Map
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
name|hdds
operator|.
name|scm
operator|.
name|net
operator|.
name|NodeSchema
operator|.
name|LayerType
import|;
end_import

begin_comment
comment|/**  * A Network topology layer schema loading tool that loads user defined network  * layer schema data from a XML configuration file.  */
end_comment

begin_class
DECL|class|NodeSchemaLoader
specifier|public
specifier|final
class|class
name|NodeSchemaLoader
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
name|NodeSchemaLoader
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|CONFIGURATION_TAG
specifier|private
specifier|static
specifier|final
name|String
name|CONFIGURATION_TAG
init|=
literal|"configuration"
decl_stmt|;
DECL|field|LAYOUT_VERSION_TAG
specifier|private
specifier|static
specifier|final
name|String
name|LAYOUT_VERSION_TAG
init|=
literal|"layoutversion"
decl_stmt|;
DECL|field|TOPOLOGY_TAG
specifier|private
specifier|static
specifier|final
name|String
name|TOPOLOGY_TAG
init|=
literal|"topology"
decl_stmt|;
DECL|field|TOPOLOGY_PATH
specifier|private
specifier|static
specifier|final
name|String
name|TOPOLOGY_PATH
init|=
literal|"path"
decl_stmt|;
DECL|field|TOPOLOGY_ENFORCE_PREFIX
specifier|private
specifier|static
specifier|final
name|String
name|TOPOLOGY_ENFORCE_PREFIX
init|=
literal|"enforceprefix"
decl_stmt|;
DECL|field|LAYERS_TAG
specifier|private
specifier|static
specifier|final
name|String
name|LAYERS_TAG
init|=
literal|"layers"
decl_stmt|;
DECL|field|LAYER_TAG
specifier|private
specifier|static
specifier|final
name|String
name|LAYER_TAG
init|=
literal|"layer"
decl_stmt|;
DECL|field|LAYER_ID
specifier|private
specifier|static
specifier|final
name|String
name|LAYER_ID
init|=
literal|"id"
decl_stmt|;
DECL|field|LAYER_TYPE
specifier|private
specifier|static
specifier|final
name|String
name|LAYER_TYPE
init|=
literal|"type"
decl_stmt|;
DECL|field|LAYER_COST
specifier|private
specifier|static
specifier|final
name|String
name|LAYER_COST
init|=
literal|"cost"
decl_stmt|;
DECL|field|LAYER_PREFIX
specifier|private
specifier|static
specifier|final
name|String
name|LAYER_PREFIX
init|=
literal|"prefix"
decl_stmt|;
DECL|field|LAYER_DEFAULT_NAME
specifier|private
specifier|static
specifier|final
name|String
name|LAYER_DEFAULT_NAME
init|=
literal|"default"
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
DECL|field|instance
specifier|private
specifier|volatile
specifier|static
name|NodeSchemaLoader
name|instance
init|=
literal|null
decl_stmt|;
DECL|method|NodeSchemaLoader ()
specifier|private
name|NodeSchemaLoader
parameter_list|()
block|{}
DECL|method|getInstance ()
specifier|public
specifier|static
name|NodeSchemaLoader
name|getInstance
parameter_list|()
block|{
if|if
condition|(
name|instance
operator|==
literal|null
condition|)
block|{
name|instance
operator|=
operator|new
name|NodeSchemaLoader
argument_list|()
expr_stmt|;
block|}
return|return
name|instance
return|;
block|}
comment|/**    * Class to house keep the result of parsing a network topology schema file.    */
DECL|class|NodeSchemaLoadResult
specifier|public
specifier|static
class|class
name|NodeSchemaLoadResult
block|{
DECL|field|schemaList
specifier|private
name|List
argument_list|<
name|NodeSchema
argument_list|>
name|schemaList
decl_stmt|;
DECL|field|enforcePrefix
specifier|private
name|boolean
name|enforcePrefix
decl_stmt|;
DECL|method|NodeSchemaLoadResult (List<NodeSchema> schemaList, boolean enforcePrefix)
name|NodeSchemaLoadResult
parameter_list|(
name|List
argument_list|<
name|NodeSchema
argument_list|>
name|schemaList
parameter_list|,
name|boolean
name|enforcePrefix
parameter_list|)
block|{
name|this
operator|.
name|schemaList
operator|=
name|schemaList
expr_stmt|;
name|this
operator|.
name|enforcePrefix
operator|=
name|enforcePrefix
expr_stmt|;
block|}
DECL|method|isEnforePrefix ()
specifier|public
name|boolean
name|isEnforePrefix
parameter_list|()
block|{
return|return
name|enforcePrefix
return|;
block|}
DECL|method|getSchemaList ()
specifier|public
name|List
argument_list|<
name|NodeSchema
argument_list|>
name|getSchemaList
parameter_list|()
block|{
return|return
name|schemaList
return|;
block|}
block|}
comment|/**    * Load user defined network layer schemas from a XML configuration file.    * @param schemaFilePath path of schema file    * @return all valid node schemas defined in schema file    */
DECL|method|loadSchemaFromFile (String schemaFilePath)
specifier|public
name|NodeSchemaLoadResult
name|loadSchemaFromFile
parameter_list|(
name|String
name|schemaFilePath
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
try|try
block|{
name|File
name|schemaFile
init|=
operator|new
name|File
argument_list|(
name|schemaFilePath
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|schemaFile
operator|.
name|exists
argument_list|()
condition|)
block|{
name|String
name|msg
init|=
literal|"Network topology layer schema file "
operator|+
name|schemaFilePath
operator|+
literal|" is not found."
decl_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
name|msg
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
return|return
name|loadSchema
argument_list|(
name|schemaFile
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
name|IllegalArgumentException
argument_list|(
literal|"Fail to load network topology node"
operator|+
literal|" schema file: "
operator|+
name|schemaFilePath
operator|+
literal|" , error:"
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/**    * Load network topology layer schemas from a XML configuration file.    * @param schemaFile schema file    * @return all valid node schemas defined in schema file    * @throws ParserConfigurationException ParserConfigurationException happen    * @throws IOException no such schema file    * @throws SAXException xml file has some invalid elements    * @throws IllegalArgumentException xml file content is logically invalid    */
DECL|method|loadSchema (File schemaFile)
specifier|private
name|NodeSchemaLoadResult
name|loadSchema
parameter_list|(
name|File
name|schemaFile
parameter_list|)
throws|throws
name|ParserConfigurationException
throws|,
name|SAXException
throws|,
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Loading network topology layer schema file "
operator|+
name|schemaFile
argument_list|)
expr_stmt|;
comment|// Read and parse the schema file.
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
name|schemaFile
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
name|CONFIGURATION_TAG
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
name|IllegalArgumentException
argument_list|(
literal|"Bad network topology layer schema "
operator|+
literal|"configuration file: top-level element not<"
operator|+
name|CONFIGURATION_TAG
operator|+
literal|">"
argument_list|)
throw|;
block|}
name|NodeSchemaLoadResult
name|schemaList
decl_stmt|;
if|if
condition|(
name|root
operator|.
name|getElementsByTagName
argument_list|(
name|LAYOUT_VERSION_TAG
argument_list|)
operator|.
name|getLength
argument_list|()
operator|==
literal|1
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
name|LAYERS_TAG
argument_list|)
operator|.
name|getLength
argument_list|()
operator|==
literal|1
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|NodeSchema
argument_list|>
name|schemas
init|=
name|loadLayersSection
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
name|TOPOLOGY_TAG
argument_list|)
operator|.
name|getLength
argument_list|()
operator|==
literal|1
condition|)
block|{
name|schemaList
operator|=
name|loadTopologySection
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
name|IllegalArgumentException
argument_list|(
literal|"Bad network topology layer "
operator|+
literal|"schema configuration file: no or multiple<"
operator|+
name|TOPOLOGY_TAG
operator|+
literal|"> element"
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
literal|"Bad network topology layer schema"
operator|+
literal|" configuration file: no or multiple<"
operator|+
name|LAYERS_TAG
operator|+
literal|">element"
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
literal|"The parse failed because of bad "
operator|+
name|LAYOUT_VERSION_TAG
operator|+
literal|" value, expected:"
operator|+
name|LAYOUT_VERSION
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
literal|"Bad network topology layer schema "
operator|+
literal|"configuration file: no or multiple<"
operator|+
name|LAYOUT_VERSION_TAG
operator|+
literal|"> elements"
argument_list|)
throw|;
block|}
return|return
name|schemaList
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
name|LAYOUT_VERSION_TAG
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
literal|"Bad "
operator|+
name|LAYOUT_VERSION_TAG
operator|+
literal|" value "
operator|+
name|value
operator|+
literal|" is found. It should be an integer."
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
literal|"Value of<"
operator|+
name|LAYOUT_VERSION_TAG
operator|+
literal|"> is null"
argument_list|)
throw|;
block|}
return|return
name|layoutVersion
return|;
block|}
comment|/**    * Load layers from root element in the XML configuration file.    * @param root root element    * @return A map of node schemas with layer ID and layer schema    */
DECL|method|loadLayersSection (Element root)
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|NodeSchema
argument_list|>
name|loadLayersSection
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
name|LAYER_TAG
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|NodeSchema
argument_list|>
name|schemas
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|NodeSchema
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
name|LAYER_TAG
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
name|layerId
init|=
name|element
operator|.
name|getAttribute
argument_list|(
name|LAYER_ID
argument_list|)
decl_stmt|;
name|NodeSchema
name|schema
init|=
name|parseLayerElement
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
name|layerId
argument_list|,
name|schema
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Repetitive layer in network "
operator|+
literal|"topology node schema configuration file: "
operator|+
name|layerId
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
literal|"Bad element in network topology "
operator|+
literal|"node schema configuration file: "
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
comment|// Integrity check, only one ROOT and one LEAF is allowed
name|boolean
name|foundRoot
init|=
literal|false
decl_stmt|;
name|boolean
name|foundLeaf
init|=
literal|false
decl_stmt|;
for|for
control|(
name|NodeSchema
name|schema
range|:
name|schemas
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|schema
operator|.
name|getType
argument_list|()
operator|==
name|LayerType
operator|.
name|ROOT
condition|)
block|{
if|if
condition|(
name|foundRoot
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Multiple ROOT layers are found"
operator|+
literal|" in network topology schema configuration file"
argument_list|)
throw|;
block|}
else|else
block|{
name|foundRoot
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
name|schema
operator|.
name|getType
argument_list|()
operator|==
name|LayerType
operator|.
name|LEAF_NODE
condition|)
block|{
if|if
condition|(
name|foundLeaf
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Multiple LEAF layers are found"
operator|+
literal|" in network topology schema configuration file"
argument_list|)
throw|;
block|}
else|else
block|{
name|foundLeaf
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
operator|!
name|foundRoot
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No ROOT layer is found"
operator|+
literal|" in network topology schema configuration file"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|foundLeaf
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No LEAF layer is found"
operator|+
literal|" in network topology schema configuration file"
argument_list|)
throw|;
block|}
return|return
name|schemas
return|;
block|}
comment|/**    * Load network topology from root element in the XML configuration file and    * sort node schemas according to the topology path.    * @param root root element    * @param schemas schema map    * @return all valid node schemas defined in schema file    */
DECL|method|loadTopologySection (Element root, Map<String, NodeSchema> schemas)
specifier|private
name|NodeSchemaLoadResult
name|loadTopologySection
parameter_list|(
name|Element
name|root
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|NodeSchema
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
name|TOPOLOGY_TAG
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
name|NodeSchema
argument_list|>
name|schemaList
init|=
operator|new
name|ArrayList
argument_list|<
name|NodeSchema
argument_list|>
argument_list|()
decl_stmt|;
name|boolean
name|enforecePrefix
init|=
literal|false
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
name|String
name|tagName
init|=
name|element
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
name|element
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
name|String
name|value
decl_stmt|;
if|if
condition|(
name|text
operator|!=
literal|null
condition|)
block|{
name|value
operator|=
name|text
operator|.
name|getData
argument_list|()
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
name|value
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// Element with empty value is ignored
continue|continue;
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
if|if
condition|(
name|TOPOLOGY_PATH
operator|.
name|equals
argument_list|(
name|tagName
argument_list|)
condition|)
block|{
if|if
condition|(
name|value
operator|.
name|startsWith
argument_list|(
name|NetConstants
operator|.
name|PATH_SEPARATOR_STR
argument_list|)
condition|)
block|{
name|value
operator|=
name|value
operator|.
name|substring
argument_list|(
literal|1
argument_list|,
name|value
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|String
index|[]
name|layerIDs
init|=
name|value
operator|.
name|split
argument_list|(
name|NetConstants
operator|.
name|PATH_SEPARATOR_STR
argument_list|)
decl_stmt|;
if|if
condition|(
name|layerIDs
operator|==
literal|null
operator|||
name|layerIDs
operator|.
name|length
operator|!=
name|schemas
operator|.
name|size
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Topology path depth doesn't "
operator|+
literal|"match layer element numbers"
argument_list|)
throw|;
block|}
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|layerIDs
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|schemas
operator|.
name|get
argument_list|(
name|layerIDs
index|[
name|j
index|]
argument_list|)
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No layer found for id "
operator|+
name|layerIDs
index|[
name|j
index|]
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|schemas
operator|.
name|get
argument_list|(
name|layerIDs
index|[
literal|0
index|]
argument_list|)
operator|.
name|getType
argument_list|()
operator|!=
name|LayerType
operator|.
name|ROOT
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Topology path doesn't start "
operator|+
literal|"with ROOT layer"
argument_list|)
throw|;
block|}
if|if
condition|(
name|schemas
operator|.
name|get
argument_list|(
name|layerIDs
index|[
name|layerIDs
operator|.
name|length
operator|-
literal|1
index|]
argument_list|)
operator|.
name|getType
argument_list|()
operator|!=
name|LayerType
operator|.
name|LEAF_NODE
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Topology path doesn't end "
operator|+
literal|"with LEAF layer"
argument_list|)
throw|;
block|}
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|layerIDs
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|schemaList
operator|.
name|add
argument_list|(
name|schemas
operator|.
name|get
argument_list|(
name|layerIDs
index|[
name|j
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|TOPOLOGY_ENFORCE_PREFIX
operator|.
name|equalsIgnoreCase
argument_list|(
name|tagName
argument_list|)
condition|)
block|{
name|enforecePrefix
operator|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
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
literal|"Unsupported Element<"
operator|+
name|tagName
operator|+
literal|">"
argument_list|)
throw|;
block|}
block|}
block|}
comment|// Integrity check
if|if
condition|(
name|enforecePrefix
condition|)
block|{
comment|// Every InnerNode should have prefix defined
for|for
control|(
name|NodeSchema
name|schema
range|:
name|schemas
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|schema
operator|.
name|getType
argument_list|()
operator|==
name|LayerType
operator|.
name|INNER_NODE
operator|&&
name|schema
operator|.
name|getPrefix
argument_list|()
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"There is layer without prefix "
operator|+
literal|"defined while prefix is enforced."
argument_list|)
throw|;
block|}
block|}
block|}
return|return
operator|new
name|NodeSchemaLoadResult
argument_list|(
name|schemaList
argument_list|,
name|enforecePrefix
argument_list|)
return|;
block|}
comment|/**    * Load a layer from a layer element in the XML configuration file.    * @param element network topology node layer element    * @return ECSchema    */
DECL|method|parseLayerElement (Element element)
specifier|private
name|NodeSchema
name|parseLayerElement
parameter_list|(
name|Element
name|element
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
name|LayerType
name|type
init|=
literal|null
decl_stmt|;
name|int
name|cost
init|=
literal|0
decl_stmt|;
name|String
name|prefix
init|=
literal|null
decl_stmt|;
name|String
name|defaultName
init|=
literal|null
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
name|String
name|value
decl_stmt|;
if|if
condition|(
name|text
operator|!=
literal|null
condition|)
block|{
name|value
operator|=
name|text
operator|.
name|getData
argument_list|()
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
name|value
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// Element with empty value is ignored
continue|continue;
block|}
block|}
else|else
block|{
continue|continue;
block|}
if|if
condition|(
name|LAYER_COST
operator|.
name|equalsIgnoreCase
argument_list|(
name|tagName
argument_list|)
condition|)
block|{
name|cost
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|value
argument_list|)
expr_stmt|;
if|if
condition|(
name|cost
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Cost should be positive number or 0"
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|LAYER_TYPE
operator|.
name|equalsIgnoreCase
argument_list|(
name|tagName
argument_list|)
condition|)
block|{
name|type
operator|=
name|NodeSchema
operator|.
name|LayerType
operator|.
name|getType
argument_list|(
name|value
argument_list|)
expr_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unsupported layer type:"
operator|+
name|value
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|LAYER_PREFIX
operator|.
name|equalsIgnoreCase
argument_list|(
name|tagName
argument_list|)
condition|)
block|{
name|prefix
operator|=
name|value
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|LAYER_DEFAULT_NAME
operator|.
name|equalsIgnoreCase
argument_list|(
name|tagName
argument_list|)
condition|)
block|{
name|defaultName
operator|=
name|value
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unsupported Element<"
operator|+
name|tagName
operator|+
literal|">"
argument_list|)
throw|;
block|}
block|}
block|}
comment|// type is a mandatory property
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Missing type Element"
argument_list|)
throw|;
block|}
return|return
operator|new
name|NodeSchema
argument_list|(
name|type
argument_list|,
name|cost
argument_list|,
name|prefix
argument_list|,
name|defaultName
argument_list|)
return|;
block|}
block|}
end_class

end_unit

