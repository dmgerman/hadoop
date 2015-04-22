begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.protocol
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
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
name|classification
operator|.
name|InterfaceStability
import|;
end_import

begin_comment
comment|/**  * Locally available datanode information  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|DatanodeLocalInfo
specifier|public
class|class
name|DatanodeLocalInfo
block|{
DECL|field|softwareVersion
specifier|private
specifier|final
name|String
name|softwareVersion
decl_stmt|;
DECL|field|configVersion
specifier|private
specifier|final
name|String
name|configVersion
decl_stmt|;
DECL|field|uptime
specifier|private
specifier|final
name|long
name|uptime
decl_stmt|;
comment|// datanode uptime in seconds.
DECL|method|DatanodeLocalInfo (String softwareVersion, String configVersion, long uptime)
specifier|public
name|DatanodeLocalInfo
parameter_list|(
name|String
name|softwareVersion
parameter_list|,
name|String
name|configVersion
parameter_list|,
name|long
name|uptime
parameter_list|)
block|{
name|this
operator|.
name|softwareVersion
operator|=
name|softwareVersion
expr_stmt|;
name|this
operator|.
name|configVersion
operator|=
name|configVersion
expr_stmt|;
name|this
operator|.
name|uptime
operator|=
name|uptime
expr_stmt|;
block|}
comment|/** get software version */
DECL|method|getSoftwareVersion ()
specifier|public
name|String
name|getSoftwareVersion
parameter_list|()
block|{
return|return
name|this
operator|.
name|softwareVersion
return|;
block|}
comment|/** get config version */
DECL|method|getConfigVersion ()
specifier|public
name|String
name|getConfigVersion
parameter_list|()
block|{
return|return
name|this
operator|.
name|configVersion
return|;
block|}
comment|/** get uptime */
DECL|method|getUptime ()
specifier|public
name|long
name|getUptime
parameter_list|()
block|{
return|return
name|this
operator|.
name|uptime
return|;
block|}
comment|/** A formatted string for printing the status of the DataNode. */
DECL|method|getDatanodeLocalReport ()
specifier|public
name|String
name|getDatanodeLocalReport
parameter_list|()
block|{
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"Uptime: "
operator|+
name|getUptime
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|", Software version: "
operator|+
name|getSoftwareVersion
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|", Config version: "
operator|+
name|getConfigVersion
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

