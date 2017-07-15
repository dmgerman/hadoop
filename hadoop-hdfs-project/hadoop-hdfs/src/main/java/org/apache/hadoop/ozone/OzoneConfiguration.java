begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
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
DECL|method|OzoneConfiguration ()
specifier|public
name|OzoneConfiguration
parameter_list|()
block|{   }
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
block|}
block|}
end_class

end_unit

