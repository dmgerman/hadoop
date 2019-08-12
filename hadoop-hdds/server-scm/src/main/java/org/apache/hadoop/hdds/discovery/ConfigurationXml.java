begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.discovery
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|discovery
package|;
end_package

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
name|List
import|;
end_import

begin_comment
comment|/**  * JAXB representation of Hadoop Configuration.  */
end_comment

begin_class
annotation|@
name|XmlRootElement
argument_list|(
name|name
operator|=
literal|"configuration"
argument_list|)
DECL|class|ConfigurationXml
specifier|public
class|class
name|ConfigurationXml
block|{
DECL|field|property
specifier|private
name|List
argument_list|<
name|ConfigurationXmlEntry
argument_list|>
name|property
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|getProperty ()
specifier|public
name|List
argument_list|<
name|ConfigurationXmlEntry
argument_list|>
name|getProperty
parameter_list|()
block|{
return|return
name|property
return|;
block|}
DECL|method|setProperty ( List<ConfigurationXmlEntry> property)
specifier|public
name|void
name|setProperty
parameter_list|(
name|List
argument_list|<
name|ConfigurationXmlEntry
argument_list|>
name|property
parameter_list|)
block|{
name|this
operator|.
name|property
operator|=
name|property
expr_stmt|;
block|}
DECL|method|addConfiguration (String key, String name)
specifier|public
name|void
name|addConfiguration
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|property
operator|.
name|add
argument_list|(
operator|new
name|ConfigurationXmlEntry
argument_list|(
name|key
argument_list|,
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

