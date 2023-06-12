import {TableColumn} from "../../shared-model";

export const TIMED_COURSE_COLUMN: TableColumn[] = [
  {name: 'ТӨРӨЛ', id: 'categoryName'},
  {name: 'ТҮЛХҮҮР ҮГ', id: 'keyword'},
  {name: 'КОД', id: 'code'},
  {name: 'НӨХЦӨЛИЙН НЭР', id: 'name'},
  {name: 'ЭХЛЭХ', id: 'startDate'},
  {name: 'ДУУСАХ', id: 'endDate'},
  {name: 'НИЙТЭЛСЭН', id: 'author'},
  {name: 'НИЙТЭЛСЭН ОГНОО', id: 'createdDate'}
]

export enum SortDirection {
  ASCENDING = 'asc',
  DESCENDING = 'desc',
  NEUTRAL = 'neutral'
}
