begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.slive
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|slive
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
name|Iterator
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
name|io
operator|.
name|Text
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
name|MapReduceBase
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
name|OutputCollector
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
name|Reducer
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
name|Reporter
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
name|util
operator|.
name|StringUtils
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

begin_comment
comment|/**  * The slive reducer which iterates over the given input values and merges them  * together into a final output value.  */
end_comment

begin_class
DECL|class|SliveReducer
specifier|public
class|class
name|SliveReducer
extends|extends
name|MapReduceBase
implements|implements
name|Reducer
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|,
name|Text
argument_list|,
name|Text
argument_list|>
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
name|SliveReducer
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|config
specifier|private
name|ConfigExtractor
name|config
decl_stmt|;
comment|/**    * Logs to the given reporter and logs to the internal logger at info level    *     * @param r    *          the reporter to set status on    * @param msg    *          the message to log    */
DECL|method|logAndSetStatus (Reporter r, String msg)
specifier|private
name|void
name|logAndSetStatus
parameter_list|(
name|Reporter
name|r
parameter_list|,
name|String
name|msg
parameter_list|)
block|{
name|r
operator|.
name|setStatus
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
comment|/**    * Fetches the config this object uses    *     * @return ConfigExtractor    */
DECL|method|getConfig ()
specifier|private
name|ConfigExtractor
name|getConfig
parameter_list|()
block|{
return|return
name|config
return|;
block|}
comment|/*    * (non-Javadoc)    *     * @see org.apache.hadoop.mapred.Reducer#reduce(java.lang.Object,    * java.util.Iterator, org.apache.hadoop.mapred.OutputCollector,    * org.apache.hadoop.mapred.Reporter)    */
annotation|@
name|Override
comment|// Reducer
DECL|method|reduce (Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter)
specifier|public
name|void
name|reduce
parameter_list|(
name|Text
name|key
parameter_list|,
name|Iterator
argument_list|<
name|Text
argument_list|>
name|values
parameter_list|,
name|OutputCollector
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
name|output
parameter_list|,
name|Reporter
name|reporter
parameter_list|)
throws|throws
name|IOException
block|{
name|OperationOutput
name|collector
init|=
literal|null
decl_stmt|;
name|int
name|reduceAm
init|=
literal|0
decl_stmt|;
name|int
name|errorAm
init|=
literal|0
decl_stmt|;
name|logAndSetStatus
argument_list|(
name|reporter
argument_list|,
literal|"Iterating over reduction values for key "
operator|+
name|key
argument_list|)
expr_stmt|;
while|while
condition|(
name|values
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Text
name|value
init|=
name|values
operator|.
name|next
argument_list|()
decl_stmt|;
try|try
block|{
name|OperationOutput
name|val
init|=
operator|new
name|OperationOutput
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|collector
operator|==
literal|null
condition|)
block|{
name|collector
operator|=
name|val
expr_stmt|;
block|}
else|else
block|{
name|collector
operator|=
name|OperationOutput
operator|.
name|merge
argument_list|(
name|collector
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Combined "
operator|+
name|val
operator|+
literal|" into/with "
operator|+
name|collector
argument_list|)
expr_stmt|;
operator|++
name|reduceAm
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
operator|++
name|errorAm
expr_stmt|;
name|logAndSetStatus
argument_list|(
name|reporter
argument_list|,
literal|"Error iterating over reduction input "
operator|+
name|value
operator|+
literal|" due to : "
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|getConfig
argument_list|()
operator|.
name|shouldExitOnFirstError
argument_list|()
condition|)
block|{
break|break;
block|}
block|}
block|}
name|logAndSetStatus
argument_list|(
name|reporter
argument_list|,
literal|"Reduced "
operator|+
name|reduceAm
operator|+
literal|" values with "
operator|+
name|errorAm
operator|+
literal|" errors"
argument_list|)
expr_stmt|;
if|if
condition|(
name|collector
operator|!=
literal|null
condition|)
block|{
name|logAndSetStatus
argument_list|(
name|reporter
argument_list|,
literal|"Writing output "
operator|+
name|collector
operator|.
name|getKey
argument_list|()
operator|+
literal|" : "
operator|+
name|collector
operator|.
name|getOutputValue
argument_list|()
argument_list|)
expr_stmt|;
name|output
operator|.
name|collect
argument_list|(
name|collector
operator|.
name|getKey
argument_list|()
argument_list|,
name|collector
operator|.
name|getOutputValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/*    * (non-Javadoc)    *     * @see    * org.apache.hadoop.mapred.MapReduceBase#configure(org.apache.hadoop.mapred    * .JobConf)    */
annotation|@
name|Override
comment|// MapReduceBase
DECL|method|configure (JobConf conf)
specifier|public
name|void
name|configure
parameter_list|(
name|JobConf
name|conf
parameter_list|)
block|{
name|config
operator|=
operator|new
name|ConfigExtractor
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|ConfigExtractor
operator|.
name|dumpOptions
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

