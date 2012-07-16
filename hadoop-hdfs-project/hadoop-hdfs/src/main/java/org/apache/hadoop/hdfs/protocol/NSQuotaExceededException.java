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

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|NSQuotaExceededException
specifier|public
specifier|final
class|class
name|NSQuotaExceededException
extends|extends
name|QuotaExceededException
block|{
DECL|field|serialVersionUID
specifier|protected
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|method|NSQuotaExceededException ()
specifier|public
name|NSQuotaExceededException
parameter_list|()
block|{}
DECL|method|NSQuotaExceededException (String msg)
specifier|public
name|NSQuotaExceededException
parameter_list|(
name|String
name|msg
parameter_list|)
block|{
name|super
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
DECL|method|NSQuotaExceededException (long quota, long count)
specifier|public
name|NSQuotaExceededException
parameter_list|(
name|long
name|quota
parameter_list|,
name|long
name|count
parameter_list|)
block|{
name|super
argument_list|(
name|quota
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getMessage ()
specifier|public
name|String
name|getMessage
parameter_list|()
block|{
name|String
name|msg
init|=
name|super
operator|.
name|getMessage
argument_list|()
decl_stmt|;
if|if
condition|(
name|msg
operator|==
literal|null
condition|)
block|{
return|return
literal|"The NameSpace quota (directories and files)"
operator|+
operator|(
name|pathName
operator|==
literal|null
condition|?
literal|""
else|:
operator|(
literal|" of directory "
operator|+
name|pathName
operator|)
operator|)
operator|+
literal|" is exceeded: quota="
operator|+
name|quota
operator|+
literal|" file count="
operator|+
name|count
return|;
block|}
else|else
block|{
return|return
name|msg
return|;
block|}
block|}
block|}
end_class

end_unit

