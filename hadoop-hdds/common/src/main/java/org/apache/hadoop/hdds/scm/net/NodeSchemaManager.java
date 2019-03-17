begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.net
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|scm
operator|.
name|net
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
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
name|hdds
operator|.
name|scm
operator|.
name|ScmConfigKeys
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
name|hdds
operator|.
name|scm
operator|.
name|net
operator|.
name|NodeSchemaLoader
operator|.
name|NodeSchemaLoadResult
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
name|conf
operator|.
name|Configuration
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
name|Arrays
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
comment|/** The class manages all network topology schemas. */
end_comment

begin_class
DECL|class|NodeSchemaManager
specifier|public
specifier|final
class|class
name|NodeSchemaManager
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|NodeSchemaManager
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// All schema saved and sorted from ROOT to LEAF node
DECL|field|allSchema
specifier|private
name|List
argument_list|<
name|NodeSchema
argument_list|>
name|allSchema
decl_stmt|;
comment|// enforcePrefix only applies to INNER_NODE
DECL|field|enforcePrefix
specifier|private
name|boolean
name|enforcePrefix
decl_stmt|;
comment|// max level, includes ROOT level
DECL|field|maxLevel
specifier|private
name|int
name|maxLevel
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|instance
specifier|private
specifier|volatile
specifier|static
name|NodeSchemaManager
name|instance
init|=
literal|null
decl_stmt|;
DECL|method|NodeSchemaManager ()
specifier|private
name|NodeSchemaManager
parameter_list|()
block|{   }
DECL|method|getInstance ()
specifier|public
specifier|static
name|NodeSchemaManager
name|getInstance
parameter_list|()
block|{
if|if
condition|(
name|instance
operator|==
literal|null
condition|)
block|{
name|instance
operator|=
operator|new
name|NodeSchemaManager
argument_list|()
expr_stmt|;
block|}
return|return
name|instance
return|;
block|}
DECL|method|init (Configuration conf)
specifier|public
name|void
name|init
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
comment|/**      * Load schemas from network topology schema configuration file      */
name|String
name|schemaFile
init|=
name|conf
operator|.
name|get
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_NETWORK_TOPOLOGY_SCHEMA_FILE
argument_list|,
name|ScmConfigKeys
operator|.
name|OZONE_SCM_NETWORK_TOPOLOGY_SCHEMA_FILE_DEFAULT
argument_list|)
decl_stmt|;
name|NodeSchemaLoadResult
name|result
decl_stmt|;
try|try
block|{
name|result
operator|=
name|NodeSchemaLoader
operator|.
name|getInstance
argument_list|()
operator|.
name|loadSchemaFromFile
argument_list|(
name|schemaFile
argument_list|)
expr_stmt|;
name|allSchema
operator|=
name|result
operator|.
name|getSchemaList
argument_list|()
expr_stmt|;
name|enforcePrefix
operator|=
name|result
operator|.
name|isEnforePrefix
argument_list|()
expr_stmt|;
name|maxLevel
operator|=
name|allSchema
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|String
name|msg
init|=
literal|"Fail to load schema file:"
operator|+
name|schemaFile
operator|+
literal|", error:"
operator|+
name|e
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|msg
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|init (NodeSchema[] schemas, boolean enforce)
specifier|public
name|void
name|init
parameter_list|(
name|NodeSchema
index|[]
name|schemas
parameter_list|,
name|boolean
name|enforce
parameter_list|)
block|{
name|allSchema
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|allSchema
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|schemas
argument_list|)
argument_list|)
expr_stmt|;
name|enforcePrefix
operator|=
name|enforce
expr_stmt|;
name|maxLevel
operator|=
name|schemas
operator|.
name|length
expr_stmt|;
block|}
DECL|method|getMaxLevel ()
specifier|public
name|int
name|getMaxLevel
parameter_list|()
block|{
return|return
name|maxLevel
return|;
block|}
DECL|method|getCost (int level)
specifier|public
name|int
name|getCost
parameter_list|(
name|int
name|level
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|level
operator|<=
name|maxLevel
operator|&&
name|level
operator|>=
operator|(
name|NetConstants
operator|.
name|ROOT_LEVEL
operator|)
argument_list|)
expr_stmt|;
return|return
name|allSchema
operator|.
name|get
argument_list|(
name|level
operator|-
name|NetConstants
operator|.
name|ROOT_LEVEL
argument_list|)
operator|.
name|getCost
argument_list|()
return|;
block|}
comment|/**    * Given a incomplete network path, return its complete network path if    * possible. E.g. input is 'node1', output is '/rack-default/node1' if this    * schema manages ROOT, RACK and LEAF, with prefix defined and enforce prefix    * enabled.    *    * @param path the incomplete input path    * @return complete path, null if cannot carry out complete action or action    * failed    */
DECL|method|complete (String path)
specifier|public
name|String
name|complete
parameter_list|(
name|String
name|path
parameter_list|)
block|{
if|if
condition|(
operator|!
name|enforcePrefix
condition|)
block|{
return|return
literal|null
return|;
block|}
name|String
name|normalizedPath
init|=
name|NetUtils
operator|.
name|normalize
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|String
index|[]
name|subPath
init|=
name|normalizedPath
operator|.
name|split
argument_list|(
name|NetConstants
operator|.
name|PATH_SEPARATOR_STR
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|subPath
operator|.
name|length
operator|)
operator|==
name|maxLevel
condition|)
block|{
return|return
name|path
return|;
block|}
name|StringBuffer
name|newPath
init|=
operator|new
name|StringBuffer
argument_list|(
name|NetConstants
operator|.
name|ROOT
argument_list|)
decl_stmt|;
comment|// skip the ROOT and LEAF layer
name|int
name|i
decl_stmt|,
name|j
decl_stmt|;
for|for
control|(
name|i
operator|=
literal|1
operator|,
name|j
operator|=
literal|1
init|;
name|i
operator|<
name|subPath
operator|.
name|length
operator|&&
name|j
operator|<
operator|(
name|allSchema
operator|.
name|size
argument_list|()
operator|-
literal|1
operator|)
condition|;
control|)
block|{
if|if
condition|(
name|allSchema
operator|.
name|get
argument_list|(
name|j
argument_list|)
operator|.
name|matchPrefix
argument_list|(
name|subPath
index|[
name|i
index|]
argument_list|)
condition|)
block|{
name|newPath
operator|.
name|append
argument_list|(
name|NetConstants
operator|.
name|PATH_SEPARATOR_STR
operator|+
name|subPath
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
name|j
operator|++
expr_stmt|;
block|}
else|else
block|{
name|newPath
operator|.
name|append
argument_list|(
name|allSchema
operator|.
name|get
argument_list|(
name|j
argument_list|)
operator|.
name|getDefaultName
argument_list|()
argument_list|)
expr_stmt|;
name|j
operator|++
expr_stmt|;
block|}
block|}
if|if
condition|(
name|i
operator|==
operator|(
name|subPath
operator|.
name|length
operator|-
literal|1
operator|)
condition|)
block|{
name|newPath
operator|.
name|append
argument_list|(
name|NetConstants
operator|.
name|PATH_SEPARATOR_STR
operator|+
name|subPath
index|[
name|i
index|]
argument_list|)
expr_stmt|;
return|return
name|newPath
operator|.
name|toString
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

