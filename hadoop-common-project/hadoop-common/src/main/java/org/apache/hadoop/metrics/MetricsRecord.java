begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * MetricsRecord.java  *  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.metrics
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics
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
comment|/**  * A named and optionally tagged set of records to be sent to the metrics  * system.<p/>  *  * A record name identifies the kind of data to be reported. For example, a  * program reporting statistics relating to the disks on a computer might use  * a record name "diskStats".<p/>  *  * A record has zero or more<i>tags</i>. A tag has a name and a value. To  * continue the example, the "diskStats" record might use a tag named  * "diskName" to identify a particular disk.  Sometimes it is useful to have  * more than one tag, so there might also be a "diskType" with value "ide" or  * "scsi" or whatever.<p/>  *  * A record also has zero or more<i>metrics</i>.  These are the named  * values that are to be reported to the metrics system.  In the "diskStats"  * example, possible metric names would be "diskPercentFull", "diskPercentBusy",   * "kbReadPerSecond", etc.<p/>  *   * The general procedure for using a MetricsRecord is to fill in its tag and  * metric values, and then call<code>update()</code> to pass the record to the  * client library.  * Metric data is not immediately sent to the metrics system  * each time that<code>update()</code> is called.   * An internal table is maintained, identified by the record name. This  * table has columns   * corresponding to the tag and the metric names, and rows   * corresponding to each unique set of tag values. An update  * either modifies an existing row in the table, or adds a new row with a set of  * tag values that are different from all the other rows.  Note that if there  * are no tags, then there can be at most one row in the table.<p/>  *   * Once a row is added to the table, its data will be sent to the metrics system   * on every timer period, whether or not it has been updated since the previous  * timer period.  If this is inappropriate, for example if metrics were being  * reported by some transient object in an application, the<code>remove()</code>  * method can be used to remove the row and thus stop the data from being  * sent.<p/>  *  * Note that the<code>update()</code> method is atomic.  This means that it is  * safe for different threads to be updating the same metric.  More precisely,  * it is OK for different threads to call<code>update()</code> on MetricsRecord instances   * with the same set of tag names and tag values.  Different threads should   *<b>not</b> use the same MetricsRecord instance at the same time.  *  * @deprecated Use {@link org.apache.hadoop.metrics2.MetricsRecord} instead.  */
end_comment

begin_interface
annotation|@
name|Deprecated
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|interface|MetricsRecord
specifier|public
interface|interface
name|MetricsRecord
block|{
comment|/**    * Returns the record name.     *    * @return the record name    */
DECL|method|getRecordName ()
specifier|public
specifier|abstract
name|String
name|getRecordName
parameter_list|()
function_decl|;
comment|/**    * Sets the named tag to the specified value.  The tagValue may be null,     * which is treated the same as an empty String.    *    * @param tagName name of the tag    * @param tagValue new value of the tag    * @throws MetricsException if the tagName conflicts with the configuration    */
DECL|method|setTag (String tagName, String tagValue)
specifier|public
specifier|abstract
name|void
name|setTag
parameter_list|(
name|String
name|tagName
parameter_list|,
name|String
name|tagValue
parameter_list|)
function_decl|;
comment|/**    * Sets the named tag to the specified value.    *    * @param tagName name of the tag    * @param tagValue new value of the tag    * @throws MetricsException if the tagName conflicts with the configuration    */
DECL|method|setTag (String tagName, int tagValue)
specifier|public
specifier|abstract
name|void
name|setTag
parameter_list|(
name|String
name|tagName
parameter_list|,
name|int
name|tagValue
parameter_list|)
function_decl|;
comment|/**    * Sets the named tag to the specified value.    *    * @param tagName name of the tag    * @param tagValue new value of the tag    * @throws MetricsException if the tagName conflicts with the configuration    */
DECL|method|setTag (String tagName, long tagValue)
specifier|public
specifier|abstract
name|void
name|setTag
parameter_list|(
name|String
name|tagName
parameter_list|,
name|long
name|tagValue
parameter_list|)
function_decl|;
comment|/**    * Sets the named tag to the specified value.    *    * @param tagName name of the tag    * @param tagValue new value of the tag    * @throws MetricsException if the tagName conflicts with the configuration    */
DECL|method|setTag (String tagName, short tagValue)
specifier|public
specifier|abstract
name|void
name|setTag
parameter_list|(
name|String
name|tagName
parameter_list|,
name|short
name|tagValue
parameter_list|)
function_decl|;
comment|/**    * Sets the named tag to the specified value.    *    * @param tagName name of the tag    * @param tagValue new value of the tag    * @throws MetricsException if the tagName conflicts with the configuration    */
DECL|method|setTag (String tagName, byte tagValue)
specifier|public
specifier|abstract
name|void
name|setTag
parameter_list|(
name|String
name|tagName
parameter_list|,
name|byte
name|tagValue
parameter_list|)
function_decl|;
comment|/**    * Removes any tag of the specified name.    *    * @param tagName name of a tag    */
DECL|method|removeTag (String tagName)
specifier|public
specifier|abstract
name|void
name|removeTag
parameter_list|(
name|String
name|tagName
parameter_list|)
function_decl|;
comment|/**    * Sets the named metric to the specified value.    *    * @param metricName name of the metric    * @param metricValue new value of the metric    * @throws MetricsException if the metricName or the type of the metricValue     * conflicts with the configuration    */
DECL|method|setMetric (String metricName, int metricValue)
specifier|public
specifier|abstract
name|void
name|setMetric
parameter_list|(
name|String
name|metricName
parameter_list|,
name|int
name|metricValue
parameter_list|)
function_decl|;
comment|/**    * Sets the named metric to the specified value.    *    * @param metricName name of the metric    * @param metricValue new value of the metric    * @throws MetricsException if the metricName or the type of the metricValue     * conflicts with the configuration    */
DECL|method|setMetric (String metricName, long metricValue)
specifier|public
specifier|abstract
name|void
name|setMetric
parameter_list|(
name|String
name|metricName
parameter_list|,
name|long
name|metricValue
parameter_list|)
function_decl|;
comment|/**    * Sets the named metric to the specified value.    *    * @param metricName name of the metric    * @param metricValue new value of the metric    * @throws MetricsException if the metricName or the type of the metricValue     * conflicts with the configuration    */
DECL|method|setMetric (String metricName, short metricValue)
specifier|public
specifier|abstract
name|void
name|setMetric
parameter_list|(
name|String
name|metricName
parameter_list|,
name|short
name|metricValue
parameter_list|)
function_decl|;
comment|/**    * Sets the named metric to the specified value.    *    * @param metricName name of the metric    * @param metricValue new value of the metric    * @throws MetricsException if the metricName or the type of the metricValue     * conflicts with the configuration    */
DECL|method|setMetric (String metricName, byte metricValue)
specifier|public
specifier|abstract
name|void
name|setMetric
parameter_list|(
name|String
name|metricName
parameter_list|,
name|byte
name|metricValue
parameter_list|)
function_decl|;
comment|/**    * Sets the named metric to the specified value.    *    * @param metricName name of the metric    * @param metricValue new value of the metric    * @throws MetricsException if the metricName or the type of the metricValue     * conflicts with the configuration    */
DECL|method|setMetric (String metricName, float metricValue)
specifier|public
specifier|abstract
name|void
name|setMetric
parameter_list|(
name|String
name|metricName
parameter_list|,
name|float
name|metricValue
parameter_list|)
function_decl|;
comment|/**    * Increments the named metric by the specified value.    *    * @param metricName name of the metric    * @param metricValue incremental value    * @throws MetricsException if the metricName or the type of the metricValue     * conflicts with the configuration    */
DECL|method|incrMetric (String metricName, int metricValue)
specifier|public
specifier|abstract
name|void
name|incrMetric
parameter_list|(
name|String
name|metricName
parameter_list|,
name|int
name|metricValue
parameter_list|)
function_decl|;
comment|/**    * Increments the named metric by the specified value.    *    * @param metricName name of the metric    * @param metricValue incremental value    * @throws MetricsException if the metricName or the type of the metricValue     * conflicts with the configuration    */
DECL|method|incrMetric (String metricName, long metricValue)
specifier|public
specifier|abstract
name|void
name|incrMetric
parameter_list|(
name|String
name|metricName
parameter_list|,
name|long
name|metricValue
parameter_list|)
function_decl|;
comment|/**    * Increments the named metric by the specified value.    *    * @param metricName name of the metric    * @param metricValue incremental value    * @throws MetricsException if the metricName or the type of the metricValue     * conflicts with the configuration    */
DECL|method|incrMetric (String metricName, short metricValue)
specifier|public
specifier|abstract
name|void
name|incrMetric
parameter_list|(
name|String
name|metricName
parameter_list|,
name|short
name|metricValue
parameter_list|)
function_decl|;
comment|/**    * Increments the named metric by the specified value.    *    * @param metricName name of the metric    * @param metricValue incremental value    * @throws MetricsException if the metricName or the type of the metricValue     * conflicts with the configuration    */
DECL|method|incrMetric (String metricName, byte metricValue)
specifier|public
specifier|abstract
name|void
name|incrMetric
parameter_list|(
name|String
name|metricName
parameter_list|,
name|byte
name|metricValue
parameter_list|)
function_decl|;
comment|/**    * Increments the named metric by the specified value.    *    * @param metricName name of the metric    * @param metricValue incremental value    * @throws MetricsException if the metricName or the type of the metricValue     * conflicts with the configuration    */
DECL|method|incrMetric (String metricName, float metricValue)
specifier|public
specifier|abstract
name|void
name|incrMetric
parameter_list|(
name|String
name|metricName
parameter_list|,
name|float
name|metricValue
parameter_list|)
function_decl|;
comment|/**    * Updates the table of buffered data which is to be sent periodically.    * If the tag values match an existing row, that row is updated;     * otherwise, a new row is added.    */
DECL|method|update ()
specifier|public
specifier|abstract
name|void
name|update
parameter_list|()
function_decl|;
comment|/**    * Removes, from the buffered data table, all rows having tags     * that equal the tags that have been set on this record. For example,    * if there are no tags on this record, all rows for this record name    * would be removed.  Or, if there is a single tag on this record, then    * just rows containing a tag with the same name and value would be removed.    */
DECL|method|remove ()
specifier|public
specifier|abstract
name|void
name|remove
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

