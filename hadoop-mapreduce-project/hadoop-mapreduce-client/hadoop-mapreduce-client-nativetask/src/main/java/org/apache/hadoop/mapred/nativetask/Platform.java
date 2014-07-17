begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.nativetask
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|nativetask
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|mapred
operator|.
name|InvalidJobConfException
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
name|mapred
operator|.
name|JobConf
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
name|mapred
operator|.
name|nativetask
operator|.
name|serde
operator|.
name|INativeSerializer
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
name|mapred
operator|.
name|nativetask
operator|.
name|serde
operator|.
name|NativeSerialization
import|;
end_import

begin_comment
comment|/**  * Base class for platforms. A platform is a framework running on top of  * MapReduce, like Hadoop, Hive, Pig, Mahout. Each framework defines its  * own key type and value type across a MapReduce job. For each platform,  * we should implement serializers such that we could communicate data with  * native side and native comparators so our native output collectors could  * sort them and write out. We've already provided the {@link HadoopPlatform}  * that supports all key types of Hadoop and users could implement their custom  * platform.  */
end_comment

begin_class
DECL|class|Platform
specifier|public
specifier|abstract
class|class
name|Platform
block|{
DECL|field|serialization
specifier|private
specifier|final
name|NativeSerialization
name|serialization
decl_stmt|;
DECL|field|keyClassNames
specifier|protected
name|Set
argument_list|<
name|String
argument_list|>
name|keyClassNames
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|Platform ()
specifier|public
name|Platform
parameter_list|()
block|{
name|this
operator|.
name|serialization
operator|=
name|NativeSerialization
operator|.
name|getInstance
argument_list|()
expr_stmt|;
block|}
comment|/**    * initialize a platform, where we should call registerKey    *    * @throws IOException    */
DECL|method|init ()
specifier|public
specifier|abstract
name|void
name|init
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * @return name of a Platform, useful for logs and debug    */
DECL|method|name ()
specifier|public
specifier|abstract
name|String
name|name
parameter_list|()
function_decl|;
comment|/**    * associate a key class with its serializer and platform    *    * @param keyClassName map out key class name    * @param key          key serializer class    * @throws IOException    */
DECL|method|registerKey (String keyClassName, Class key)
specifier|protected
name|void
name|registerKey
parameter_list|(
name|String
name|keyClassName
parameter_list|,
name|Class
name|key
parameter_list|)
throws|throws
name|IOException
block|{
name|serialization
operator|.
name|register
argument_list|(
name|keyClassName
argument_list|,
name|key
argument_list|)
expr_stmt|;
name|keyClassNames
operator|.
name|add
argument_list|(
name|keyClassName
argument_list|)
expr_stmt|;
block|}
comment|/**    * whether a platform supports a specific key should at least satisfy two conditions    *    * 1. the key belongs to the platform    * 2. the associated serializer must implement {@link INativeComparable} interface    *    *    * @param keyClassName map out put key class name    * @param serializer   serializer associated with key via registerKey    * @param job          job configuration    * @return             true if the platform has implemented native comparators of the key and    *                     false otherwise    */
DECL|method|support (String keyClassName, INativeSerializer serializer, JobConf job)
specifier|protected
specifier|abstract
name|boolean
name|support
parameter_list|(
name|String
name|keyClassName
parameter_list|,
name|INativeSerializer
name|serializer
parameter_list|,
name|JobConf
name|job
parameter_list|)
function_decl|;
comment|/**    * whether it's the platform that has defined a custom Java comparator    *    * NativeTask doesn't support custom Java comparator(set with mapreduce.job.output.key.comparator.class)    * but a platform (e.g Pig) could also set that conf and implement native comparators so    * we shouldn't bail out.    *    * @param keyComparator comparator set with mapreduce.job.output.key.comparator.class    * @return    */
DECL|method|define (Class keyComparator)
specifier|protected
specifier|abstract
name|boolean
name|define
parameter_list|(
name|Class
name|keyComparator
parameter_list|)
function_decl|;
block|}
end_class

end_unit

