begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.uploader
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|uploader
package|;
end_package

begin_comment
comment|/**  * Default white list and black list implementations.  */
end_comment

begin_class
DECL|class|DefaultJars
specifier|final
class|class
name|DefaultJars
block|{
DECL|field|DEFAULT_EXCLUDED_MR_JARS
specifier|static
specifier|final
name|String
name|DEFAULT_EXCLUDED_MR_JARS
init|=
literal|".*hadoop-yarn-server-applicationhistoryservice.*\\.jar,"
operator|+
literal|".*hadoop-yarn-server-nodemanager.*\\.jar,"
operator|+
literal|".*hadoop-yarn-server-resourcemanager.*\\.jar,"
operator|+
literal|".*hadoop-yarn-server-router.*\\.jar,"
operator|+
literal|".*hadoop-yarn-server-sharedcachemanager.*\\.jar,"
operator|+
literal|".*hadoop-yarn-server-timeline-pluginstorage.*\\.jar,"
operator|+
literal|".*hadoop-yarn-server-timelineservice.*\\.jar,"
operator|+
literal|".*hadoop-yarn-server-timelineservice-hbase.*\\.jar,"
decl_stmt|;
DECL|field|DEFAULT_MR_JARS
specifier|static
specifier|final
name|String
name|DEFAULT_MR_JARS
init|=
literal|"$HADOOP_HOME/share/hadoop/common/.*\\.jar,"
operator|+
literal|"$HADOOP_HOME/share/hadoop/common/lib/.*\\.jar,"
operator|+
literal|"$HADOOP_HOME/share/hadoop/hdfs/.*\\.jar,"
operator|+
literal|"$HADOOP_HOME/share/hadoop/hdfs/lib/.*\\.jar,"
operator|+
literal|"$HADOOP_HOME/share/hadoop/mapreduce/.*\\.jar,"
operator|+
literal|"$HADOOP_HOME/share/hadoop/mapreduce/lib/.*\\.jar,"
operator|+
literal|"$HADOOP_HOME/share/hadoop/yarn/.*\\.jar,"
operator|+
literal|"$HADOOP_HOME/share/hadoop/yarn/lib/.*\\.jar,"
decl_stmt|;
DECL|method|DefaultJars ()
specifier|private
name|DefaultJars
parameter_list|()
block|{}
block|}
end_class

end_unit

