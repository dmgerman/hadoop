begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.app.webapp.dao
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|webapp
operator|.
name|dao
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
name|XmlRootElement
import|;
end_import

begin_class
annotation|@
name|XmlRootElement
annotation|@
name|XmlAccessorType
argument_list|(
name|XmlAccessType
operator|.
name|FIELD
argument_list|)
DECL|class|ConfEntryInfo
specifier|public
class|class
name|ConfEntryInfo
block|{
DECL|field|name
specifier|protected
name|String
name|name
decl_stmt|;
DECL|field|value
specifier|protected
name|String
name|value
decl_stmt|;
DECL|field|source
specifier|protected
name|String
index|[]
name|source
decl_stmt|;
DECL|method|ConfEntryInfo ()
specifier|public
name|ConfEntryInfo
parameter_list|()
block|{   }
DECL|method|ConfEntryInfo (String key, String value)
specifier|public
name|ConfEntryInfo
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|this
argument_list|(
name|key
argument_list|,
name|value
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|ConfEntryInfo (String key, String value, String[] source)
specifier|public
name|ConfEntryInfo
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|value
parameter_list|,
name|String
index|[]
name|source
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|key
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
block|}
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|this
operator|.
name|name
return|;
block|}
DECL|method|getValue ()
specifier|public
name|String
name|getValue
parameter_list|()
block|{
return|return
name|this
operator|.
name|value
return|;
block|}
DECL|method|getSource ()
specifier|public
name|String
index|[]
name|getSource
parameter_list|()
block|{
return|return
name|source
return|;
block|}
block|}
end_class

end_unit

