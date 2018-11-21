begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|om
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
name|annotation
operator|.
name|JsonProperty
import|;
end_import

begin_comment
comment|/**  * OmMetricsInfo stored in a file, which will be used during OM restart to  * initialize the metrics. Currently this stores only numKeys.  */
end_comment

begin_class
DECL|class|OmMetricsInfo
specifier|public
class|class
name|OmMetricsInfo
block|{
annotation|@
name|JsonProperty
DECL|field|numKeys
specifier|private
name|long
name|numKeys
decl_stmt|;
DECL|method|OmMetricsInfo ()
name|OmMetricsInfo
parameter_list|()
block|{
name|this
operator|.
name|numKeys
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|getNumKeys ()
specifier|public
name|long
name|getNumKeys
parameter_list|()
block|{
return|return
name|numKeys
return|;
block|}
DECL|method|setNumKeys (long numKeys)
specifier|public
name|void
name|setNumKeys
parameter_list|(
name|long
name|numKeys
parameter_list|)
block|{
name|this
operator|.
name|numKeys
operator|=
name|numKeys
expr_stmt|;
block|}
block|}
end_class

end_unit

