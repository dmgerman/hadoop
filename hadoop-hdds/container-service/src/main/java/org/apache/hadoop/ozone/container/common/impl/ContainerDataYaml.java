begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common.impl
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|impl
package|;
end_package

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
name|protocol
operator|.
name|datanode
operator|.
name|proto
operator|.
name|ContainerProtos
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
name|protocol
operator|.
name|datanode
operator|.
name|proto
operator|.
name|ContainerProtos
operator|.
name|ContainerType
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
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|StorageContainerException
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
name|ozone
operator|.
name|OzoneConsts
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
name|ozone
operator|.
name|container
operator|.
name|keyvalue
operator|.
name|KeyValueContainerData
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
name|org
operator|.
name|yaml
operator|.
name|snakeyaml
operator|.
name|Yaml
import|;
end_import

begin_import
import|import
name|java
operator|.
name|beans
operator|.
name|IntrospectionException
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
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
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
name|io
operator|.
name|FileOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStreamWriter
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
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
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
name|yaml
operator|.
name|snakeyaml
operator|.
name|constructor
operator|.
name|AbstractConstruct
import|;
end_import

begin_import
import|import
name|org
operator|.
name|yaml
operator|.
name|snakeyaml
operator|.
name|constructor
operator|.
name|Constructor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|yaml
operator|.
name|snakeyaml
operator|.
name|introspector
operator|.
name|BeanAccess
import|;
end_import

begin_import
import|import
name|org
operator|.
name|yaml
operator|.
name|snakeyaml
operator|.
name|introspector
operator|.
name|Property
import|;
end_import

begin_import
import|import
name|org
operator|.
name|yaml
operator|.
name|snakeyaml
operator|.
name|introspector
operator|.
name|PropertyUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|yaml
operator|.
name|snakeyaml
operator|.
name|nodes
operator|.
name|MappingNode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|yaml
operator|.
name|snakeyaml
operator|.
name|nodes
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|org
operator|.
name|yaml
operator|.
name|snakeyaml
operator|.
name|nodes
operator|.
name|ScalarNode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|yaml
operator|.
name|snakeyaml
operator|.
name|nodes
operator|.
name|Tag
import|;
end_import

begin_import
import|import
name|org
operator|.
name|yaml
operator|.
name|snakeyaml
operator|.
name|representer
operator|.
name|Representer
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
name|ozone
operator|.
name|container
operator|.
name|keyvalue
operator|.
name|KeyValueContainerData
operator|.
name|KEYVALUE_YAML_TAG
import|;
end_import

begin_comment
comment|/**  * Class for creating and reading .container files.  */
end_comment

begin_class
DECL|class|ContainerDataYaml
specifier|public
specifier|final
class|class
name|ContainerDataYaml
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
name|ContainerDataYaml
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|ContainerDataYaml ()
specifier|private
name|ContainerDataYaml
parameter_list|()
block|{    }
comment|/**    * Creates a .container file in yaml format.    *    * @param containerFile    * @param containerData    * @throws IOException    */
DECL|method|createContainerFile (ContainerType containerType, ContainerData containerData, File containerFile)
specifier|public
specifier|static
name|void
name|createContainerFile
parameter_list|(
name|ContainerType
name|containerType
parameter_list|,
name|ContainerData
name|containerData
parameter_list|,
name|File
name|containerFile
parameter_list|)
throws|throws
name|IOException
block|{
name|Writer
name|writer
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// Create Yaml for given container type
name|Yaml
name|yaml
init|=
name|getYamlForContainerType
argument_list|(
name|containerType
argument_list|)
decl_stmt|;
comment|// Compute Checksum and update ContainerData
name|containerData
operator|.
name|computeAndSetChecksum
argument_list|(
name|yaml
argument_list|)
expr_stmt|;
comment|// Write the ContainerData with checksum to Yaml file.
name|writer
operator|=
operator|new
name|OutputStreamWriter
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|containerFile
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|yaml
operator|.
name|dump
argument_list|(
name|containerData
argument_list|,
name|writer
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
try|try
block|{
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
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error occurred during closing the writer. ContainerID: "
operator|+
name|containerData
operator|.
name|getContainerID
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Read the yaml file, and return containerData.    *    * @param containerFile    * @throws IOException    */
DECL|method|readContainerFile (File containerFile)
specifier|public
specifier|static
name|ContainerData
name|readContainerFile
parameter_list|(
name|File
name|containerFile
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|containerFile
argument_list|,
literal|"containerFile cannot be null"
argument_list|)
expr_stmt|;
name|InputStream
name|input
init|=
literal|null
decl_stmt|;
name|ContainerData
name|containerData
decl_stmt|;
try|try
block|{
name|PropertyUtils
name|propertyUtils
init|=
operator|new
name|PropertyUtils
argument_list|()
decl_stmt|;
name|propertyUtils
operator|.
name|setBeanAccess
argument_list|(
name|BeanAccess
operator|.
name|FIELD
argument_list|)
expr_stmt|;
name|propertyUtils
operator|.
name|setAllowReadOnlyProperties
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Representer
name|representer
init|=
operator|new
name|ContainerDataRepresenter
argument_list|()
decl_stmt|;
name|representer
operator|.
name|setPropertyUtils
argument_list|(
name|propertyUtils
argument_list|)
expr_stmt|;
name|Constructor
name|containerDataConstructor
init|=
operator|new
name|ContainerDataConstructor
argument_list|()
decl_stmt|;
name|Yaml
name|yaml
init|=
operator|new
name|Yaml
argument_list|(
name|containerDataConstructor
argument_list|,
name|representer
argument_list|)
decl_stmt|;
name|yaml
operator|.
name|setBeanAccess
argument_list|(
name|BeanAccess
operator|.
name|FIELD
argument_list|)
expr_stmt|;
name|input
operator|=
operator|new
name|FileInputStream
argument_list|(
name|containerFile
argument_list|)
expr_stmt|;
name|containerData
operator|=
operator|(
name|ContainerData
operator|)
name|yaml
operator|.
name|load
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|input
operator|!=
literal|null
condition|)
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|containerData
return|;
block|}
comment|/**    * Given a ContainerType this method returns a Yaml representation of    * the container properties.    *    * @param containerType type of container    * @return Yamal representation of container properties    *    * @throws StorageContainerException if the type is unrecognized    */
DECL|method|getYamlForContainerType (ContainerType containerType)
specifier|public
specifier|static
name|Yaml
name|getYamlForContainerType
parameter_list|(
name|ContainerType
name|containerType
parameter_list|)
throws|throws
name|StorageContainerException
block|{
name|PropertyUtils
name|propertyUtils
init|=
operator|new
name|PropertyUtils
argument_list|()
decl_stmt|;
name|propertyUtils
operator|.
name|setBeanAccess
argument_list|(
name|BeanAccess
operator|.
name|FIELD
argument_list|)
expr_stmt|;
name|propertyUtils
operator|.
name|setAllowReadOnlyProperties
argument_list|(
literal|true
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|containerType
condition|)
block|{
case|case
name|KeyValueContainer
case|:
name|Representer
name|representer
init|=
operator|new
name|ContainerDataRepresenter
argument_list|()
decl_stmt|;
name|representer
operator|.
name|setPropertyUtils
argument_list|(
name|propertyUtils
argument_list|)
expr_stmt|;
name|representer
operator|.
name|addClassTag
argument_list|(
name|KeyValueContainerData
operator|.
name|class
argument_list|,
name|KeyValueContainerData
operator|.
name|KEYVALUE_YAML_TAG
argument_list|)
expr_stmt|;
name|Constructor
name|keyValueDataConstructor
init|=
operator|new
name|ContainerDataConstructor
argument_list|()
decl_stmt|;
return|return
operator|new
name|Yaml
argument_list|(
name|keyValueDataConstructor
argument_list|,
name|representer
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|StorageContainerException
argument_list|(
literal|"Unrecognized container Type "
operator|+
literal|"format "
operator|+
name|containerType
argument_list|,
name|ContainerProtos
operator|.
name|Result
operator|.
name|UNKNOWN_CONTAINER_TYPE
argument_list|)
throw|;
block|}
block|}
comment|/**    * Representer class to define which fields need to be stored in yaml file.    */
DECL|class|ContainerDataRepresenter
specifier|private
specifier|static
class|class
name|ContainerDataRepresenter
extends|extends
name|Representer
block|{
annotation|@
name|Override
DECL|method|getProperties (Class<? extends Object> type)
specifier|protected
name|Set
argument_list|<
name|Property
argument_list|>
name|getProperties
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Object
argument_list|>
name|type
parameter_list|)
throws|throws
name|IntrospectionException
block|{
name|Set
argument_list|<
name|Property
argument_list|>
name|set
init|=
name|super
operator|.
name|getProperties
argument_list|(
name|type
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|Property
argument_list|>
name|filtered
init|=
operator|new
name|TreeSet
argument_list|<
name|Property
argument_list|>
argument_list|()
decl_stmt|;
comment|// When a new Container type is added, we need to add what fields need
comment|// to be filtered here
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
name|KeyValueContainerData
operator|.
name|class
argument_list|)
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|yamlFields
init|=
name|KeyValueContainerData
operator|.
name|getYamlFields
argument_list|()
decl_stmt|;
comment|// filter properties
for|for
control|(
name|Property
name|prop
range|:
name|set
control|)
block|{
name|String
name|name
init|=
name|prop
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|yamlFields
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|filtered
operator|.
name|add
argument_list|(
name|prop
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|filtered
return|;
block|}
block|}
comment|/**    * Constructor class for KeyValueData, which will be used by Yaml.    */
DECL|class|ContainerDataConstructor
specifier|private
specifier|static
class|class
name|ContainerDataConstructor
extends|extends
name|Constructor
block|{
DECL|method|ContainerDataConstructor ()
name|ContainerDataConstructor
parameter_list|()
block|{
comment|//Adding our own specific constructors for tags.
comment|// When a new Container type is added, we need to add yamlConstructor
comment|// for that
name|this
operator|.
name|yamlConstructors
operator|.
name|put
argument_list|(
name|KEYVALUE_YAML_TAG
argument_list|,
operator|new
name|ConstructKeyValueContainerData
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|yamlConstructors
operator|.
name|put
argument_list|(
name|Tag
operator|.
name|INT
argument_list|,
operator|new
name|ConstructLong
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|class|ConstructKeyValueContainerData
specifier|private
class|class
name|ConstructKeyValueContainerData
extends|extends
name|AbstractConstruct
block|{
DECL|method|construct (Node node)
specifier|public
name|Object
name|construct
parameter_list|(
name|Node
name|node
parameter_list|)
block|{
name|MappingNode
name|mnode
init|=
operator|(
name|MappingNode
operator|)
name|node
decl_stmt|;
name|Map
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|nodes
init|=
name|constructMapping
argument_list|(
name|mnode
argument_list|)
decl_stmt|;
comment|//Needed this, as TAG.INT type is by default converted to Long.
name|long
name|layOutVersion
init|=
operator|(
name|long
operator|)
name|nodes
operator|.
name|get
argument_list|(
name|OzoneConsts
operator|.
name|LAYOUTVERSION
argument_list|)
decl_stmt|;
name|int
name|lv
init|=
operator|(
name|int
operator|)
name|layOutVersion
decl_stmt|;
name|long
name|size
init|=
operator|(
name|long
operator|)
name|nodes
operator|.
name|get
argument_list|(
name|OzoneConsts
operator|.
name|MAX_SIZE_GB
argument_list|)
decl_stmt|;
name|int
name|maxSize
init|=
operator|(
name|int
operator|)
name|size
decl_stmt|;
comment|//When a new field is added, it needs to be added here.
name|KeyValueContainerData
name|kvData
init|=
operator|new
name|KeyValueContainerData
argument_list|(
operator|(
name|long
operator|)
name|nodes
operator|.
name|get
argument_list|(
name|OzoneConsts
operator|.
name|CONTAINER_ID
argument_list|)
argument_list|,
name|lv
argument_list|,
name|maxSize
argument_list|)
decl_stmt|;
name|kvData
operator|.
name|setContainerDBType
argument_list|(
operator|(
name|String
operator|)
name|nodes
operator|.
name|get
argument_list|(
name|OzoneConsts
operator|.
name|CONTAINER_DB_TYPE
argument_list|)
argument_list|)
expr_stmt|;
name|kvData
operator|.
name|setMetadataPath
argument_list|(
operator|(
name|String
operator|)
name|nodes
operator|.
name|get
argument_list|(
name|OzoneConsts
operator|.
name|METADATA_PATH
argument_list|)
argument_list|)
expr_stmt|;
name|kvData
operator|.
name|setChunksPath
argument_list|(
operator|(
name|String
operator|)
name|nodes
operator|.
name|get
argument_list|(
name|OzoneConsts
operator|.
name|CHUNKS_PATH
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|meta
init|=
operator|(
name|Map
operator|)
name|nodes
operator|.
name|get
argument_list|(
name|OzoneConsts
operator|.
name|METADATA
argument_list|)
decl_stmt|;
name|kvData
operator|.
name|setMetadata
argument_list|(
name|meta
argument_list|)
expr_stmt|;
name|kvData
operator|.
name|setChecksum
argument_list|(
operator|(
name|String
operator|)
name|nodes
operator|.
name|get
argument_list|(
name|OzoneConsts
operator|.
name|CHECKSUM
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|state
init|=
operator|(
name|String
operator|)
name|nodes
operator|.
name|get
argument_list|(
name|OzoneConsts
operator|.
name|STATE
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|state
condition|)
block|{
case|case
literal|"OPEN"
case|:
name|kvData
operator|.
name|setState
argument_list|(
name|ContainerProtos
operator|.
name|ContainerLifeCycleState
operator|.
name|OPEN
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"CLOSING"
case|:
name|kvData
operator|.
name|setState
argument_list|(
name|ContainerProtos
operator|.
name|ContainerLifeCycleState
operator|.
name|CLOSING
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"CLOSED"
case|:
name|kvData
operator|.
name|setState
argument_list|(
name|ContainerProtos
operator|.
name|ContainerLifeCycleState
operator|.
name|CLOSED
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Unexpected "
operator|+
literal|"ContainerLifeCycleState "
operator|+
name|state
operator|+
literal|" for the containerId "
operator|+
name|nodes
operator|.
name|get
argument_list|(
name|OzoneConsts
operator|.
name|CONTAINER_ID
argument_list|)
argument_list|)
throw|;
block|}
return|return
name|kvData
return|;
block|}
block|}
comment|//Below code is taken from snake yaml, as snakeyaml tries to fit the
comment|// number if it fits in integer, otherwise returns long. So, slightly
comment|// modified the code to return long in all cases.
DECL|class|ConstructLong
specifier|private
class|class
name|ConstructLong
extends|extends
name|AbstractConstruct
block|{
DECL|method|construct (Node node)
specifier|public
name|Object
name|construct
parameter_list|(
name|Node
name|node
parameter_list|)
block|{
name|String
name|value
init|=
name|constructScalar
argument_list|(
operator|(
name|ScalarNode
operator|)
name|node
argument_list|)
operator|.
name|toString
argument_list|()
operator|.
name|replaceAll
argument_list|(
literal|"_"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|int
name|sign
init|=
operator|+
literal|1
decl_stmt|;
name|char
name|first
init|=
name|value
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|first
operator|==
literal|'-'
condition|)
block|{
name|sign
operator|=
operator|-
literal|1
expr_stmt|;
name|value
operator|=
name|value
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|first
operator|==
literal|'+'
condition|)
block|{
name|value
operator|=
name|value
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|int
name|base
init|=
literal|10
decl_stmt|;
if|if
condition|(
literal|"0"
operator|.
name|equals
argument_list|(
name|value
argument_list|)
condition|)
block|{
return|return
name|Long
operator|.
name|valueOf
argument_list|(
literal|0
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|value
operator|.
name|startsWith
argument_list|(
literal|"0b"
argument_list|)
condition|)
block|{
name|value
operator|=
name|value
operator|.
name|substring
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|base
operator|=
literal|2
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|.
name|startsWith
argument_list|(
literal|"0x"
argument_list|)
condition|)
block|{
name|value
operator|=
name|value
operator|.
name|substring
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|base
operator|=
literal|16
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|.
name|startsWith
argument_list|(
literal|"0"
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
argument_list|)
expr_stmt|;
name|base
operator|=
literal|8
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|String
index|[]
name|digits
init|=
name|value
operator|.
name|split
argument_list|(
literal|":"
argument_list|)
decl_stmt|;
name|int
name|bes
init|=
literal|1
decl_stmt|;
name|int
name|val
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|,
name|j
init|=
name|digits
operator|.
name|length
init|;
name|i
operator|<
name|j
condition|;
name|i
operator|++
control|)
block|{
name|val
operator|+=
operator|(
name|Long
operator|.
name|parseLong
argument_list|(
name|digits
index|[
operator|(
name|j
operator|-
name|i
operator|)
operator|-
literal|1
index|]
argument_list|)
operator|*
name|bes
operator|)
expr_stmt|;
name|bes
operator|*=
literal|60
expr_stmt|;
block|}
return|return
name|createNumber
argument_list|(
name|sign
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|val
argument_list|)
argument_list|,
literal|10
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|createNumber
argument_list|(
name|sign
argument_list|,
name|value
argument_list|,
literal|10
argument_list|)
return|;
block|}
return|return
name|createNumber
argument_list|(
name|sign
argument_list|,
name|value
argument_list|,
name|base
argument_list|)
return|;
block|}
block|}
DECL|method|createNumber (int sign, String number, int radix)
specifier|private
name|Number
name|createNumber
parameter_list|(
name|int
name|sign
parameter_list|,
name|String
name|number
parameter_list|,
name|int
name|radix
parameter_list|)
block|{
name|Number
name|result
decl_stmt|;
if|if
condition|(
name|sign
operator|<
literal|0
condition|)
block|{
name|number
operator|=
literal|"-"
operator|+
name|number
expr_stmt|;
block|}
name|result
operator|=
name|Long
operator|.
name|valueOf
argument_list|(
name|number
argument_list|,
name|radix
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
block|}
end_class

end_unit

