begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.conf
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
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
name|conf
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|JAXBContext
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|JAXBException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|Unmarshaller
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlAccessType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlAccessorType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlElement
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlRootElement
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
name|ArrayList
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
name|Properties
import|;
end_import

begin_comment
comment|/**  * Configuration for ozone.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|OzoneConfiguration
specifier|public
class|class
name|OzoneConfiguration
extends|extends
name|Configuration
block|{
static|static
block|{
name|activate
argument_list|()
expr_stmt|;
block|}
DECL|method|OzoneConfiguration ()
specifier|public
name|OzoneConfiguration
parameter_list|()
block|{
name|OzoneConfiguration
operator|.
name|activate
argument_list|()
expr_stmt|;
block|}
DECL|method|OzoneConfiguration (Configuration conf)
specifier|public
name|OzoneConfiguration
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|super
argument_list|(
name|conf
argument_list|)
expr_stmt|;
comment|//load the configuration from the classloader of the original conf.
name|setClassLoader
argument_list|(
name|conf
operator|.
name|getClassLoader
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|readPropertyFromXml (URL url)
specifier|public
name|List
argument_list|<
name|Property
argument_list|>
name|readPropertyFromXml
parameter_list|(
name|URL
name|url
parameter_list|)
throws|throws
name|JAXBException
block|{
name|JAXBContext
name|context
init|=
name|JAXBContext
operator|.
name|newInstance
argument_list|(
name|XMLConfiguration
operator|.
name|class
argument_list|)
decl_stmt|;
name|Unmarshaller
name|um
init|=
name|context
operator|.
name|createUnmarshaller
argument_list|()
decl_stmt|;
name|XMLConfiguration
name|config
init|=
operator|(
name|XMLConfiguration
operator|)
name|um
operator|.
name|unmarshal
argument_list|(
name|url
argument_list|)
decl_stmt|;
return|return
name|config
operator|.
name|getProperties
argument_list|()
return|;
block|}
comment|/**    * Class to marshall/un-marshall configuration from xml files.    */
annotation|@
name|XmlAccessorType
argument_list|(
name|XmlAccessType
operator|.
name|FIELD
argument_list|)
annotation|@
name|XmlRootElement
argument_list|(
name|name
operator|=
literal|"configuration"
argument_list|)
DECL|class|XMLConfiguration
specifier|public
specifier|static
class|class
name|XMLConfiguration
block|{
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"property"
argument_list|,
name|type
operator|=
name|Property
operator|.
name|class
argument_list|)
DECL|field|properties
specifier|private
name|List
argument_list|<
name|Property
argument_list|>
name|properties
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|XMLConfiguration ()
specifier|public
name|XMLConfiguration
parameter_list|()
block|{     }
DECL|method|XMLConfiguration (List<Property> properties)
specifier|public
name|XMLConfiguration
parameter_list|(
name|List
argument_list|<
name|Property
argument_list|>
name|properties
parameter_list|)
block|{
name|this
operator|.
name|properties
operator|=
name|properties
expr_stmt|;
block|}
DECL|method|getProperties ()
specifier|public
name|List
argument_list|<
name|Property
argument_list|>
name|getProperties
parameter_list|()
block|{
return|return
name|properties
return|;
block|}
DECL|method|setProperties (List<Property> properties)
specifier|public
name|void
name|setProperties
parameter_list|(
name|List
argument_list|<
name|Property
argument_list|>
name|properties
parameter_list|)
block|{
name|this
operator|.
name|properties
operator|=
name|properties
expr_stmt|;
block|}
block|}
comment|/**    * Class to marshall/un-marshall configuration properties from xml files.    */
annotation|@
name|XmlAccessorType
argument_list|(
name|XmlAccessType
operator|.
name|FIELD
argument_list|)
annotation|@
name|XmlRootElement
argument_list|(
name|name
operator|=
literal|"property"
argument_list|)
DECL|class|Property
specifier|public
specifier|static
class|class
name|Property
implements|implements
name|Comparable
argument_list|<
name|Property
argument_list|>
block|{
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
DECL|field|value
specifier|private
name|String
name|value
decl_stmt|;
DECL|field|tag
specifier|private
name|String
name|tag
decl_stmt|;
DECL|field|description
specifier|private
name|String
name|description
decl_stmt|;
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|setName (String name)
specifier|public
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
DECL|method|getValue ()
specifier|public
name|String
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
DECL|method|setValue (String value)
specifier|public
name|void
name|setValue
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
DECL|method|getTag ()
specifier|public
name|String
name|getTag
parameter_list|()
block|{
return|return
name|tag
return|;
block|}
DECL|method|setTag (String tag)
specifier|public
name|void
name|setTag
parameter_list|(
name|String
name|tag
parameter_list|)
block|{
name|this
operator|.
name|tag
operator|=
name|tag
expr_stmt|;
block|}
DECL|method|getDescription ()
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
name|description
return|;
block|}
DECL|method|setDescription (String description)
specifier|public
name|void
name|setDescription
parameter_list|(
name|String
name|description
parameter_list|)
block|{
name|this
operator|.
name|description
operator|=
name|description
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|compareTo (Property o)
specifier|public
name|int
name|compareTo
parameter_list|(
name|Property
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
name|this
operator|.
name|getName
argument_list|()
operator|.
name|compareTo
argument_list|(
name|o
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|this
operator|.
name|getName
argument_list|()
operator|+
literal|" "
operator|+
name|this
operator|.
name|getValue
argument_list|()
operator|+
literal|" "
operator|+
name|this
operator|.
name|getTag
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|this
operator|.
name|getName
argument_list|()
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object obj)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
return|return
operator|(
name|obj
operator|instanceof
name|Property
operator|)
operator|&&
operator|(
operator|(
operator|(
name|Property
operator|)
name|obj
operator|)
operator|.
name|getName
argument_list|()
operator|)
operator|.
name|equals
argument_list|(
name|this
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
DECL|method|activate ()
specifier|public
specifier|static
name|void
name|activate
parameter_list|()
block|{
comment|// adds the default resources
name|Configuration
operator|.
name|addDefaultResource
argument_list|(
literal|"hdfs-default.xml"
argument_list|)
expr_stmt|;
name|Configuration
operator|.
name|addDefaultResource
argument_list|(
literal|"hdfs-site.xml"
argument_list|)
expr_stmt|;
name|Configuration
operator|.
name|addDefaultResource
argument_list|(
literal|"ozone-default.xml"
argument_list|)
expr_stmt|;
name|Configuration
operator|.
name|addDefaultResource
argument_list|(
literal|"ozone-site.xml"
argument_list|)
expr_stmt|;
block|}
comment|/**    * The super class method getAllPropertiesByTag    * does not override values of properties    * if there is no tag present in the configs of    * newly added resources.    * @param tag    * @return Properties that belong to the tag    */
annotation|@
name|Override
DECL|method|getAllPropertiesByTag (String tag)
specifier|public
name|Properties
name|getAllPropertiesByTag
parameter_list|(
name|String
name|tag
parameter_list|)
block|{
comment|// Call getProps first to load the newly added resources
comment|// before calling super.getAllPropertiesByTag
name|Properties
name|updatedProps
init|=
name|getProps
argument_list|()
decl_stmt|;
name|Properties
name|propertiesByTag
init|=
name|super
operator|.
name|getAllPropertiesByTag
argument_list|(
name|tag
argument_list|)
decl_stmt|;
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|Enumeration
name|properties
init|=
name|propertiesByTag
operator|.
name|propertyNames
argument_list|()
decl_stmt|;
while|while
condition|(
name|properties
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|Object
name|propertyName
init|=
name|properties
operator|.
name|nextElement
argument_list|()
decl_stmt|;
comment|// get the current value of the property
name|Object
name|value
init|=
name|updatedProps
operator|.
name|getProperty
argument_list|(
name|propertyName
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
name|props
operator|.
name|put
argument_list|(
name|propertyName
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|props
return|;
block|}
block|}
end_class

end_unit

