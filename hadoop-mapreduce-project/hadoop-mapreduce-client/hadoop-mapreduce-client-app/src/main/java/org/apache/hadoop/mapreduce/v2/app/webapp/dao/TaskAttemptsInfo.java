begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by taskattemptlicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|XmlElementRef
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
argument_list|(
name|name
operator|=
literal|"taskAttempts"
argument_list|)
DECL|class|TaskAttemptsInfo
specifier|public
class|class
name|TaskAttemptsInfo
block|{
DECL|field|taskAttempts
specifier|protected
name|List
argument_list|<
name|TaskAttemptInfo
argument_list|>
name|taskAttempts
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|TaskAttemptsInfo ()
specifier|public
name|TaskAttemptsInfo
parameter_list|()
block|{   }
comment|// JAXB needs this
DECL|method|add (TaskAttemptInfo taskattemptInfo)
specifier|public
name|void
name|add
parameter_list|(
name|TaskAttemptInfo
name|taskattemptInfo
parameter_list|)
block|{
name|taskAttempts
operator|.
name|add
argument_list|(
name|taskattemptInfo
argument_list|)
expr_stmt|;
block|}
comment|// XmlElementRef annotation should be used to identify the exact type of a list element
comment|// otherwise metadata will be added to XML attributes,
comment|// it can lead to incorrect JSON marshaling
annotation|@
name|XmlElementRef
DECL|method|getTaskAttempts ()
specifier|public
name|List
argument_list|<
name|TaskAttemptInfo
argument_list|>
name|getTaskAttempts
parameter_list|()
block|{
return|return
name|taskAttempts
return|;
block|}
block|}
end_class

end_unit

