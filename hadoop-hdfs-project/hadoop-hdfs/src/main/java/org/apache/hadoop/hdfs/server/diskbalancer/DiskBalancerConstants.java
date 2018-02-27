begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p/>  * http://www.apache.org/licenses/LICENSE-2.0  *<p/>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.diskbalancer
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|diskbalancer
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
comment|/**  * Constants used by Disk Balancer.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|DiskBalancerConstants
specifier|public
specifier|final
class|class
name|DiskBalancerConstants
block|{
DECL|field|DISKBALANCER_BANDWIDTH
specifier|public
specifier|static
specifier|final
name|String
name|DISKBALANCER_BANDWIDTH
init|=
literal|"DiskBalancerBandwidth"
decl_stmt|;
DECL|field|DISKBALANCER_VOLUME_NAME
specifier|public
specifier|static
specifier|final
name|String
name|DISKBALANCER_VOLUME_NAME
init|=
literal|"DiskBalancerVolumeName"
decl_stmt|;
comment|/** Min and Max Plan file versions that we know of. **/
DECL|field|DISKBALANCER_MIN_VERSION
specifier|public
specifier|static
specifier|final
name|int
name|DISKBALANCER_MIN_VERSION
init|=
literal|1
decl_stmt|;
DECL|field|DISKBALANCER_MAX_VERSION
specifier|public
specifier|static
specifier|final
name|int
name|DISKBALANCER_MAX_VERSION
init|=
literal|1
decl_stmt|;
comment|// never constructed.
DECL|method|DiskBalancerConstants ()
specifier|private
name|DiskBalancerConstants
parameter_list|()
block|{   }
block|}
end_class

end_unit

