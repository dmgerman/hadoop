begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode.top
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
name|namenode
operator|.
name|top
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetAddress
import|;
end_import

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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|FileStatus
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
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|AuditLogger
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
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|top
operator|.
name|metrics
operator|.
name|TopMetrics
import|;
end_import

begin_comment
comment|/**  * An {@link AuditLogger} that sends logged data directly to the metrics  * systems. It is used when the top service is used directly by the name node  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|TopAuditLogger
specifier|public
class|class
name|TopAuditLogger
implements|implements
name|AuditLogger
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TopAuditLogger
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|topMetrics
specifier|private
specifier|final
name|TopMetrics
name|topMetrics
decl_stmt|;
DECL|method|TopAuditLogger (TopMetrics topMetrics)
specifier|public
name|TopAuditLogger
parameter_list|(
name|TopMetrics
name|topMetrics
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|topMetrics
argument_list|,
literal|"Cannot init with a null "
operator|+
literal|"TopMetrics"
argument_list|)
expr_stmt|;
name|this
operator|.
name|topMetrics
operator|=
name|topMetrics
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|initialize (Configuration conf)
specifier|public
name|void
name|initialize
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{   }
annotation|@
name|Override
DECL|method|logAuditEvent (boolean succeeded, String userName, InetAddress addr, String cmd, String src, String dst, FileStatus status)
specifier|public
name|void
name|logAuditEvent
parameter_list|(
name|boolean
name|succeeded
parameter_list|,
name|String
name|userName
parameter_list|,
name|InetAddress
name|addr
parameter_list|,
name|String
name|cmd
parameter_list|,
name|String
name|src
parameter_list|,
name|String
name|dst
parameter_list|,
name|FileStatus
name|status
parameter_list|)
block|{
try|try
block|{
name|topMetrics
operator|.
name|report
argument_list|(
name|succeeded
argument_list|,
name|userName
argument_list|,
name|addr
argument_list|,
name|cmd
argument_list|,
name|src
argument_list|,
name|dst
argument_list|,
name|status
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"An error occurred while reflecting the event in top service, "
operator|+
literal|"event: (cmd={},userName={})"
argument_list|,
name|cmd
argument_list|,
name|userName
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"allowed="
argument_list|)
operator|.
name|append
argument_list|(
name|succeeded
argument_list|)
operator|.
name|append
argument_list|(
literal|"\t"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"ugi="
argument_list|)
operator|.
name|append
argument_list|(
name|userName
argument_list|)
operator|.
name|append
argument_list|(
literal|"\t"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"ip="
argument_list|)
operator|.
name|append
argument_list|(
name|addr
argument_list|)
operator|.
name|append
argument_list|(
literal|"\t"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"cmd="
argument_list|)
operator|.
name|append
argument_list|(
name|cmd
argument_list|)
operator|.
name|append
argument_list|(
literal|"\t"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"src="
argument_list|)
operator|.
name|append
argument_list|(
name|src
argument_list|)
operator|.
name|append
argument_list|(
literal|"\t"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"dst="
argument_list|)
operator|.
name|append
argument_list|(
name|dst
argument_list|)
operator|.
name|append
argument_list|(
literal|"\t"
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|==
name|status
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"perm=null"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"perm="
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|status
operator|.
name|getOwner
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|":"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|status
operator|.
name|getGroup
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|":"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|status
operator|.
name|getPermission
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"------------------- logged event for top service: "
operator|+
name|sb
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

