begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.web.resources
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|web
operator|.
name|resources
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|client
operator|.
name|HdfsClientConfigKeys
operator|.
name|DFS_REPLICATION_DEFAULT
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|client
operator|.
name|HdfsClientConfigKeys
operator|.
name|DFS_REPLICATION_KEY
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
comment|/** Replication parameter. */
end_comment

begin_class
DECL|class|ReplicationParam
specifier|public
class|class
name|ReplicationParam
extends|extends
name|ShortParam
block|{
comment|/** Parameter name. */
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"replication"
decl_stmt|;
comment|/** Default parameter value. */
DECL|field|DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT
init|=
name|NULL
decl_stmt|;
DECL|field|DOMAIN
specifier|private
specifier|static
specifier|final
name|Domain
name|DOMAIN
init|=
operator|new
name|Domain
argument_list|(
name|NAME
argument_list|)
decl_stmt|;
comment|/**    * Constructor.    * @param value the parameter value.    */
DECL|method|ReplicationParam (final Short value)
specifier|public
name|ReplicationParam
parameter_list|(
specifier|final
name|Short
name|value
parameter_list|)
block|{
name|super
argument_list|(
name|DOMAIN
argument_list|,
name|value
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructor.    * @param str a string representation of the parameter value.    */
DECL|method|ReplicationParam (final String str)
specifier|public
name|ReplicationParam
parameter_list|(
specifier|final
name|String
name|str
parameter_list|)
block|{
name|this
argument_list|(
name|DOMAIN
operator|.
name|parse
argument_list|(
name|str
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|NAME
return|;
block|}
comment|/** @return the value or, if it is null, return the default from conf. */
DECL|method|getValue (final Configuration conf)
specifier|public
name|short
name|getValue
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|)
block|{
return|return
name|getValue
argument_list|()
operator|!=
literal|null
condition|?
name|getValue
argument_list|()
else|:
operator|(
name|short
operator|)
name|conf
operator|.
name|getInt
argument_list|(
name|DFS_REPLICATION_KEY
argument_list|,
name|DFS_REPLICATION_DEFAULT
argument_list|)
return|;
block|}
block|}
end_class

end_unit

